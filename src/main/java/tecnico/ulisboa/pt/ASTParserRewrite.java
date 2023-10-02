package tecnico.ulisboa.pt;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ASTParserRewrite extends ModifierVisitor<Void> {
    private Lattice lattice;
    public CompilationUnit cu;
    private String combination;
    private String main_class = "";
    private HashMap<String, List<BodyDeclaration<?>>> custom_classes = new HashMap<>();
    private HashMap<String, String> variable_level = new HashMap<>();
    private HashMap<String, String> declassification_variables = new HashMap<>();
    private Stack<String> declassification_stack = new Stack<>();
    private Integer count_declassification;
    private boolean is_main;


    public ASTParserRewrite(CompilationUnit cu, Lattice l, String main_class, HashMap<String, List<BodyDeclaration<?>>> custom_classes) {
        this.lattice = l;
        this.combination = l.getCombination();
        this.count_declassification = 0;
        this.cu = cu;
        this.main_class = main_class;
        this.custom_classes = custom_classes;

        this.visit(this.cu, null);
    }

    @Override
    public Visitable visit(MethodDeclaration c, Void arg) {
        this.variable_level.clear();
        this.declassification_variables.clear();
        this.declassification_stack.clear();

        if (!c.getNameAsString().equals("main")) {
            this.is_main = false;
            for(Parameter parameter : c.getParameters()) {
                this.variable_level.put(parameter.getNameAsString(), parameter.getTypeAsString());
            }
        } else {
            this.is_main = true;
        }

        super.visit(c, arg);
        return c;
    }

    public String lastDeclassification(String variable) {
        String last_declassification = "";
        Stack<String> auxStack = new Stack<>();
        auxStack.addAll(this.declassification_stack);
        while (!auxStack.isEmpty()) {
            last_declassification = auxStack.pop();
            if (last_declassification.contains(variable)) {
                return last_declassification;
            }
        }
        return variable;

    }

    @Override
    public Visitable visit(LineComment c, Void arg) {
        String regex = "declassification\\(([^,]+),([^,]+)\\)\\s*\\{";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(c.getContent());

        if (this.lattice.getMatrix().containsKey(c.getContent())) {
            this.variableDeclarationRewrite((VariableDeclarationExpr) c.getCommentedNode().get().getChildNodes().get(0), c.getContent());
        } else if (matcher.matches()) {
            String variable = matcher.group(1).trim();
            String new_level = matcher.group(2).trim();

            this.count_declassification = this.count_declassification + 1;
            this.declassification_variables.put(variable + "_" + this.count_declassification, variable);
            this.declassification_stack.push(variable + "_" + this.count_declassification);

            String last_level = this.variable_level.get(variable);

            String custom_class = "";
            if (last_level.lastIndexOf("_") != -1) {
                custom_class = last_level.substring(0, last_level.lastIndexOf("_"));
                if (!new_level.equals(this.lattice.getTop())) {
                    this.variable_level.put(variable + "_" + this.count_declassification, last_level.substring(0, last_level.lastIndexOf("_")) + "_" + new_level);
                } else {
                    this.variable_level.put(variable + "_" + this.count_declassification, last_level.substring(0, last_level.lastIndexOf("_")));
                }
            } else {
                custom_class = last_level;
                if (!new_level.equals(this.lattice.getTop())) {
                    this.variable_level.put(variable + "_" + this.count_declassification, last_level + "_" + new_level);
                } else {
                    this.variable_level.put(variable + "_" + this.count_declassification, last_level);
                }
            }

            String new_custom_class = "";
            if (new_level.equals(this.lattice.getTop())) {
                new_custom_class = custom_class;
            } else {
                new_custom_class = custom_class + "_" + new_level;
            }

            VariableDeclarationExpr declaration = new VariableDeclarationExpr(new ClassOrInterfaceType(new_custom_class),
                    variable + "_" + this.count_declassification);

            String statement = "new " + new_custom_class + "(";
            for (BodyDeclaration<?> field : this.custom_classes.get(custom_class)) {
                statement = statement + variable + "." + field.toFieldDeclaration().get().getVariables().get(0).toString() + ", ";
            }
            statement = statement.substring(0, statement.length() - 2);
            statement = statement + ")";

            declaration.getVariable(0).setInitializer(statement);
            ExpressionStmt newStmt = new ExpressionStmt(declaration);

            c.getCommentedNode().get().findAll(NameExpr.class).forEach(n -> {
                if (n.getNameAsString().contains(variable)) {
                    n.setName(variable + "_" + this.count_declassification);
                }
            });

            BlockStmt block = c.getCommentedNode().get().findAncestor(BlockStmt.class).get();
            int count = 0;
            BlockStmt newBlock = new BlockStmt();
            newBlock.copyStatements(block);

            for (Statement st : newBlock.getStatements()) {
                if (st.equals(c.getCommentedNode().get())) {
                    block.addStatement(count, newStmt);
                }
                count = count + 1;
            }

        } else if (c.getContent().equals("}")) {
            String declassification_variable = this.declassification_stack.pop();
            String variable = this.declassification_variables.get(declassification_variable);
            c.getCommentedNode().get().findAll(NameExpr.class).forEach(n -> {
                if (n.getNameAsString().equals(declassification_variable)) {
                    n.setName(this.lastDeclassification(variable));
                    this.declassification_variables.remove(declassification_variable);
                }
            });

            c.getCommentedNode().get().findAll(CastExpr.class).forEach(a -> {
                a.setType(variable_level.get(this.lastDeclassification(variable)));
            });
        }

        super.visit(c, arg);
        return c;
    }

    @Override
    public Visitable visit(AssignExpr a, Void arg) {
        this.assignmentExprRewrite(a);
        super.visit(a, arg);
        return a;
    }

    public Visitable visit(NameExpr a, Void arg) {
        if(a.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            changeNameExprDeclassification(a);
        }
        super.visit(a, arg);
        return a;
    }


    private String combinationResult(String level1, String level2) {
        String result = "";
        if (this.combination.equals("meet")) {
            result = this.lattice.meet(level1, level2);
        } else {
            result = this.lattice.join(level1, level2);
        }
        return result;
    }

    private void variableDeclarationRewrite(VariableDeclarationExpr expr, String level) {
        VariableDeclarator variable = (VariableDeclarator) expr.getChildNodes().get(0);

        String classLevel = "";
        String initializer = variable.getInitializer().get().toString();

        if (!level.equals(this.lattice.getTop())) {
            classLevel = variable.getType().toString() + "_" + level;
        } else {
            classLevel = variable.getType().toString();
        }

        this.variable_level.put(variable.getNameAsString().toString(), classLevel);
        variable.setType(classLevel);

        if (variable.getInitializer().get().isObjectCreationExpr()) {
            variable.setInitializer("new " + variable.getType().toString() + initializer.substring(initializer.indexOf("("), initializer.lastIndexOf(")") + 1));
        } else {
            variable.setInitializer(new CastExpr(variable.getType(), variable.getInitializer().get()));
        }
    }

    private void assignmentExprRewrite(AssignExpr expr) {
        if (expr.getTarget().isNameExpr() && !expr.getValue().isNameExpr() && is_main) {
            expr.setValue(new CastExpr(new ClassOrInterfaceType(this.variable_level.get(this.lastDeclassification(expr.getTarget().toString()))), expr.getValue().clone()));
        } else if (expr.getTarget().isNameExpr() && !is_main) {
            expr.setValue(new CastExpr(new ClassOrInterfaceType(this.variable_level.get(this.lastDeclassification(expr.getTarget().toString()))), expr.getValue().clone()));
        }
    }

    private void changeNameExprDeclassification(NameExpr name) {
        for (int i = this.count_declassification; i > 0; i--) {
            if (this.declassification_stack.search(name.getNameAsString() + "_" + i) != -1) {
                name.setName(name.getNameAsString() + "_" + i);
                break;
            }
        }
    }

    public String toString() {
        /*YamlPrinter printer = new YamlPrinter(true);
        return printer.output(cu);*/
        return cu.toString();
    }
}

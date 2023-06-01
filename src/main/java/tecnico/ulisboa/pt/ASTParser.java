package tecnico.ulisboa.pt;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.VarType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.printer.YamlPrinter;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.javaparser.ast.expr.AssignExpr.Operator.ASSIGN;

public class ASTParser extends ModifierVisitor<Void> {
    private Lattice lattice;
    public CompilationUnit cu;
    private HashMap<String, List<BodyDeclaration<?>>> custom_classes = new HashMap<>();

    private String combination;

    @Override
    public Visitable visit(ClassOrInterfaceDeclaration c, Void arg) {
        if (!c.getNameAsString().equals("Application_Linear")) {
            List<BodyDeclaration<?>> fields = c.getMembers().stream().filter(n -> n.isFieldDeclaration()).collect(Collectors.toList());
            this.custom_classes.put(c.getNameAsString(), fields);
            this.addMethods(c, combination(c.getNameAsString(), this.lattice.getTop(), this.combination));
            this.SecurityLevelsToClasses(c);
        }
        super.visit(c, arg);
        return c;
    }

    @Override
    public Visitable visit(LineComment c, Void arg) {
        if (this.lattice.getMatrix().containsKey(c.getContent())) {
            this.variableDeclarationRewrite((VariableDeclarationExpr) c.getCommentedNode().get().getChildNodes().get(0), c.getContent());
        }
        super.visit(c, arg);
        return c;
    }

    public Visitable visit(AssignExpr a, Void arg) {
        if (a.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString().equals("Application_Linear")) {
            this.assignmentExprRewrite(a);
        }
        super.visit(a, arg);
        return a;
    }

    public ASTParser(Lattice l, String filename, String combination) {
        this.lattice = l;
        this.combination = combination;

        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(Main.class).resolve("src/main/resources"));
        this.cu = sourceRoot.parse("", filename);

        this.visit(cu, null);
    }

    private void addMethods(ClassOrInterfaceDeclaration node, NodeList<MethodDeclaration> methods) {
        for (MethodDeclaration method : methods) {
            MethodDeclaration addedMethod = node.addMethod(method.getNameAsString());
            addedMethod.setModifiers(method.getModifiers());
            addedMethod.setType(method.getType());
            addedMethod.setParameters(method.getParameters());
            addedMethod.setBody(method.getBody().get());
        }
    }

    private NodeList<MethodDeclaration> combination(String class_name, String class_level, String combination) {
        NodeList<MethodDeclaration> methods = new NodeList<>();

        if (this.custom_classes.keySet().contains(class_name)) {
            Map<String, List<String>> Matrix = this.lattice.getMatrix();

            for (String level : Matrix.keySet()) {
                MethodDeclaration method = new MethodDeclaration();
                method.setName("combine");
                method.setModifiers(Modifier.Keyword.PUBLIC);

                String type = "";
                String parameter = "";
                String result = "";
                if (combination.equals("meet")) {
                    result = lattice.meet(class_level, level);
                } else {
                    result = lattice.join(class_level, level);
                }

                if (!result.equals(lattice.getTop())) {
                    type = class_name + "_" + result;
                } else {
                    type = class_name;
                }

                if (!level.equals(lattice.getTop())) {
                    parameter = class_name + "_" + level;
                } else {
                    parameter = class_name;
                }

                method.setType(new TypeParameter(type));
                method.setParameters(new NodeList<Parameter>(new Parameter(new TypeParameter(parameter), "x")));

                BlockStmt body = new BlockStmt();
                String statement = "return ";

                if (!result.equals(class_level)) {
                    statement = statement + "new " + type + "(";
                    for (BodyDeclaration<?> field : this.custom_classes.get(class_name)) {
                        statement = statement + "this." + field.toFieldDeclaration().get().getVariables().get(0).toString() + ", ";
                    }
                    statement = statement.substring(0, statement.length() - 2);
                    statement = statement + ")";
                } else {
                    statement = statement + "this";
                }

                statement = statement + ";";
                body.addStatement(statement);

                method.setBody(body);

                methods.add(method);
            }
        }
        return methods;
    }

    private void SecurityLevelsToClasses(ClassOrInterfaceDeclaration node) {
        Map<String, List<String>> Matrix = this.lattice.getMatrix();

        for (String level : Matrix.keySet()) {
            if (!level.equals(this.lattice.getTop())) {
                ClassOrInterfaceDeclaration newClass = new ClassOrInterfaceDeclaration();
                ConstructorDeclaration newConstructor = newClass.addConstructor(Modifier.Keyword.PUBLIC);
                newClass.setInterface(false);
                newClass.setName(node.getNameAsString() + "_" + level);

                newConstructor.setName(node.getNameAsString() + "_" + level);

                NodeList<Parameter> arguments = new NodeList<>();

                BlockStmt body = new BlockStmt();
                String statement = "super(";

                for (BodyDeclaration<?> field : this.custom_classes.get(node.getNameAsString())) {
                    arguments.add(new Parameter(new TypeParameter(field.toFieldDeclaration().get().getCommonType().toString()),
                            field.toFieldDeclaration().get().getVariables().get(0).toString()));
                    statement = statement + "this." + field.toFieldDeclaration().get().getVariables().get(0).toString() + ", ";
                }

                newConstructor.setParameters(arguments);

                statement = statement.substring(0, statement.length() - 2);
                statement = statement + ");";
                body.addStatement(statement);
                newConstructor.setBody(body);

                NodeList<ClassOrInterfaceType> inheritance = new NodeList<>();
                for (String inheritedLevel : Matrix.get(level)) {
                    inheritance.add(new ClassOrInterfaceType(node.getNameAsString() + "_" + inheritedLevel));
                }

                this.addMethods(newClass, this.combination(node.getNameAsString(), level, this.combination));

                newClass.setExtendedTypes(inheritance);

                cu.getTypes().addAfter(newClass, node);
            }
        }
    }

    private void variableDeclarationRewrite(VariableDeclarationExpr expr, String level) {
        VariableDeclarator variable = (VariableDeclarator) expr.getChildNodes().get(0);
        String classLevel = "";
        String initializer = variable.getInitializer().toString();

        if (!level.equals(this.lattice.getTop())) {
            classLevel = variable.getType().toString() + "_" + level;
        } else {
            classLevel = variable.getType().toString();
        }

        variable.setType(classLevel);
        variable.setInitializer("new " + variable.getType().toString() + initializer.substring(initializer.indexOf("("), initializer.lastIndexOf(")")+1));
    }

    private void createMethodExpression(ArrayList<Expression> variables, MethodCallExpr valueExpr, int index) {
        if (index + 1 < variables.size()) {
            MethodCallExpr newExpr = new MethodCallExpr();
            newExpr.setScope(variables.get(index));
            newExpr.setName("combine");
            valueExpr.setArguments(new NodeList<Expression>(newExpr));
            createMethodExpression(variables, newExpr, index + 1);
        } else {
            valueExpr.setArguments(new NodeList<Expression>(variables.get(index)));
        }
    }

    private void assignmentExprRewrite(AssignExpr expr) {
        ArrayList<Expression> variables = new ArrayList<>();
        if (!expr.getValue().getClass().getSimpleName().equals("NameExpr")) {
            variables.addAll(expr.getValue().findAll(NameExpr.class));
            MethodCallExpr valueExpr = new MethodCallExpr();
            valueExpr.setName("combine");
            valueExpr.setScope(variables.get(0));
            createMethodExpression(variables, valueExpr, 1);

            ExpressionStmt newStmt = new ExpressionStmt(new AssignExpr(new NameExpr(expr.getTarget().toString()), valueExpr, ASSIGN));
            BlockStmt block = expr.findAncestor(BlockStmt.class).get();
            int index = 0;
            for (Statement s : block.getStatements()) {
                if (s.isExpressionStmt()) {
                    if (s.asExpressionStmt().getExpression().toString().equals(expr.toString())) {
                        break;
                    }
                }
                index = index + 1;
            }
            block.addStatement(index, newStmt);
        }
    }

    public String toString() {
        /*YamlPrinter printer = new YamlPrinter(true);
        return printer.output(cu);*/
        return cu.toString();
    }
}

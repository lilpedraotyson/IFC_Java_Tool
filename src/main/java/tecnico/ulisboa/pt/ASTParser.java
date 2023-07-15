package tecnico.ulisboa.pt;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.javaparser.ast.expr.AssignExpr.Operator.ASSIGN;

public class ASTParser extends ModifierVisitor<Void> {
    private Lattice lattice;
    public CompilationUnit cu;
    private String combination;
    private HashMap<String, List<BodyDeclaration<?>>> custom_classes = new HashMap<>();
    private HashMap<String, String> variable_level = new HashMap<>();


    public ASTParser(Lattice l, String filename, String combination) {
        this.lattice = l;
        this.combination = combination;

        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(Main.class).resolve("src/main/resources"));
        this.cu = sourceRoot.parse("", filename);

        this.visit(cu, null);
    }

    @Override
    public Visitable visit(ClassOrInterfaceDeclaration c, Void arg) {
        if (!c.getNameAsString().equals("Application")) {
            List<BodyDeclaration<?>> fields = c.getMembers().stream().filter(n -> n.isFieldDeclaration()).collect(Collectors.toList());
            this.custom_classes.put(c.getNameAsString(), fields);

            this.SecurityLevelsToClasses(c);
            this.overrideMethods(c.getNameAsString(), c.getMethods(), true, this.lattice.getTop());

            MethodDeclaration addedMethod = c.addMethod("level");
            addedMethod.setModifiers(Modifier.Keyword.PUBLIC);
            addedMethod.setType("int");
            BlockStmt body = new BlockStmt();
            body.addStatement(new ReturnStmt(this.lattice.getLevelDepht().get(this.lattice.getTop()).toString()));
            addedMethod.setBody(body);
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

    @Override
    public Visitable visit(AssignExpr a, Void arg) {
        if (a.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString().equals("Application")) {
            this.assignmentExprRewrite(a);
        }
        super.visit(a, arg);
        return a;
    }

    private void addMethods(ClassOrInterfaceDeclaration node, List<MethodDeclaration> methods) {
        for (MethodDeclaration method : methods) {
            MethodDeclaration addedMethod = node.addMethod(method.getNameAsString());
            addedMethod.setModifiers(method.getModifiers());
            addedMethod.setType(method.getType());
            addedMethod.setParameters(method.getParameters());
            addedMethod.setBody(method.getBody().get());
        }
    }

    private void SecurityLevelsToClasses(ClassOrInterfaceDeclaration node) {
        Map<String, String> Matrix = this.lattice.getMatrix();

        for (String level : Matrix.keySet()) {
            if (!level.equals(this.lattice.getTop())) {
                ClassOrInterfaceDeclaration newClass = new ClassOrInterfaceDeclaration();
                ConstructorDeclaration newConstructor = newClass.addConstructor(Modifier.Keyword.PUBLIC);
                MethodDeclaration levelMethod = newClass.addMethod("level");
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

                if (Matrix.get(level).equals(this.lattice.getTop())) {
                    inheritance.add(new ClassOrInterfaceType(node.getNameAsString()));
                } else {
                    inheritance.add(new ClassOrInterfaceType(node.getNameAsString() + "_" + Matrix.get(level)));
                }

                levelMethod.setModifiers(Modifier.Keyword.PUBLIC);
                levelMethod.setType("int");
                BlockStmt body_method = new BlockStmt();
                body_method.addStatement(new ReturnStmt(this.lattice.getLevelDepht().get(level).toString().toString()));
                levelMethod.setBody(body_method);

                newClass.setExtendedTypes(inheritance);

                addMethods(newClass, node.getMethods());
                this.overrideMethods(node.getNameAsString(), newClass.getMethods(), false, level);

                cu.getTypes().addAfter(newClass, node);
            }
        }
    }

    private String combinationResult(String level1, String level2) {
        String result = "";
        if (this.combination.equals("meet")) {
            result = this.lattice.meet(level1, level2);
        } else {
            result = this.lattice.join(level1, level2);
        }
        return result.equals(this.lattice.getTop()) ? "" : result;
    }

    private void addReturnStatement(NodeList<Statement> statements, MethodDeclaration method, Statement current_statement,
                                    String class_level, String class_name, String level) {

        statements.add(new ExpressionStmt(new AssignExpr(new NameExpr(method.getTypeAsString() + " return_statement"),
                current_statement.asReturnStmt().getExpression().get(), ASSIGN)));

        String expression = method.getTypeAsString() + "_" + combinationResult(level, class_level) + "(";

        for (BodyDeclaration<?> field : this.custom_classes.get(class_name)) {
            expression = expression + "return_statement." + field.toFieldDeclaration().get().getVariables().get(0).toString() + ", ";
        }
        expression = expression.substring(0, expression.length() - 2);
        expression = expression + ")";

        statements.add(new ReturnStmt(expression));
    }

    private void changeIfStatement(MethodDeclaration method, IfStmt if_statement, String class_level,
                                   String class_name, String level) {

        NodeList<Statement> statements = new NodeList<>();

        if (if_statement.hasThenBlock()) {
            for (Statement statement : if_statement.getThenStmt().asBlockStmt().getStatements()) {
                if (statement.isReturnStmt()) {
                    addReturnStatement(statements, method, statement, class_level, class_name, level);
                } else {
                    if (statement.isIfStmt()) {
                        changeIfStatement(method, statement.asIfStmt(), class_level, class_name, level);
                    }
                    statements.add(statement);
                }
            }
            BlockStmt newThenBlock = new BlockStmt();
            newThenBlock.copyStatements(statements);
            if_statement.setThenStmt(newThenBlock);
        }

        if (if_statement.hasElseBlock()) {
            for (Statement statement : if_statement.getElseStmt().get().asBlockStmt().getStatements()) {
                if (statement.isReturnStmt()) {
                    addReturnStatement(statements, method, statement, class_level, class_name, level);
                } else {
                    if (statement.isIfStmt()) {
                        changeIfStatement(method, statement.asIfStmt(), class_level, class_name, level);
                    }
                    statements.add(statement);
                }
            }
            BlockStmt newElseBlock = new BlockStmt();
            newElseBlock.copyStatements(statements);
            if_statement.setElseStmt(newElseBlock);
        }
    }

    private void addOverride(String class_level, String class_name, MethodDeclaration method) {
        BlockStmt body = method.getBody().get();
        SwitchStmt sw = new SwitchStmt();
        NodeList<SwitchEntry> entrys = new NodeList<>();

        NodeList<Expression> s_selector_arguments = new NodeList<>();
        s_selector_arguments.add(new NameExpr("this.level()"));
        for (Parameter par : method.getParameters()) {
            s_selector_arguments.add(new NameExpr(par.getNameAsString() + ".level()"));
        }

        if (this.combination.equals("meet")) {
            sw.setSelector(new MethodCallExpr(new NameExpr("Math"), "min", s_selector_arguments));
        } else if (this.combination.equals("join")) {
            sw.setSelector(new MethodCallExpr(new NameExpr("Math"), "max", s_selector_arguments));
        }

        for (String level : this.lattice.getMatrix().keySet()) {
            NodeList<Statement> statements = new NodeList<>();
            BlockStmt newBody = new BlockStmt();
            newBody.copyStatements(body);

            if (!level.equals(class_level)) {
                for (Statement statement : newBody.getStatements()) {
                    if (statement.isReturnStmt()) {
                        addReturnStatement(statements, method, statement, class_level, class_name, level);
                    } else {
                        if (statement.isIfStmt()) {
                            changeIfStatement(method, statement.asIfStmt(), class_level, class_name, level);
                        }
                        statements.add(statement);
                    }
                }
                entrys.add(new SwitchEntry(new NodeList<>(new IntegerLiteralExpr(this.lattice.getLevelDepht().get(level)))
                                          , SwitchEntry.Type.STATEMENT_GROUP , statements));
            }
        }

        sw.setEntries(entrys);
        method.setBody(new BlockStmt(new NodeList<Statement>(sw)));
    }

    private void overrideMethods(String class_name, List<MethodDeclaration> methods, boolean top_class, String level) {
        if (top_class == false) {
            for (MethodDeclaration method : methods) {
                method.addAnnotation("Override");
                if (!method.getNameAsString().equals("level") && !method.getType().isVoidType())
                    addOverride(level, class_name, method);
            }
        } else {
            for (MethodDeclaration method : methods) {
                if (!method.getNameAsString().equals("level") && !method.getType().isVoidType())
                    addOverride(level, class_name, method);
            }
        }
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
        if (expr.getTarget().isNameExpr() && !expr.getValue().isNameExpr()) {
            expr.setValue(new CastExpr(new ClassOrInterfaceType(this.variable_level.get(expr.getTarget().toString())), expr.getValue()));
        }
    }

    public String toString() {
        /*YamlPrinter printer = new YamlPrinter(true);
        return printer.output(cu);*/
        return cu.toString();
    }
}

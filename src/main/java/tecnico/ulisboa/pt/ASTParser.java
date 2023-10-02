package tecnico.ulisboa.pt;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.javaparser.ast.expr.AssignExpr.Operator.ASSIGN;

public class ASTParser extends ModifierVisitor<Void> {
    private Lattice lattice;
    private CompilationUnit cu;
    private String combination;
    private String main_class = "";
    private HashMap<String, List<BodyDeclaration<?>>> custom_classes = new HashMap<>();
    private Integer count_return_statement = 0;


    public ASTParser(Lattice l, String filename) {
        this.lattice = l;
        this.combination = l.getCombination();

        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(Main.class).resolve(filename.substring(0, filename.lastIndexOf("/") + 1)));
        this.cu = sourceRoot.parse("", filename.substring(filename.lastIndexOf("/") + 1));

        List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class).stream().collect(Collectors.toList());
        List<MethodDeclaration> methods;
        for (ClassOrInterfaceDeclaration c : classes) {
            methods = c.getMethods();
            for (MethodDeclaration m : methods) {
                if (m.getNameAsString().equals("main")) {
                    main_class = c.getNameAsString();
                }
            }
            if (!c.getNameAsString().equals(main_class)) {
                List<BodyDeclaration<?>> fields = c.getMembers().stream().filter(n -> n.isFieldDeclaration()).collect(Collectors.toList());
                this.custom_classes.put(c.getNameAsString(), fields);
            }
        }

        ASTParserRewrite ast = new ASTParserRewrite(this.cu, this.lattice, this.main_class, this.custom_classes);
        this.visit(this.cu, null);
    }

    @Override
    public Visitable visit(ClassOrInterfaceDeclaration c, Void arg) {
        if (!c.getNameAsString().equals(main_class)) {
            this.SecurityLevelsToClasses(c);
            this.overrideMethods(c.getMethods(), true, this.lattice.getTop());

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
                    statement = statement  + field.toFieldDeclaration().get().getVariables().get(0).toString() + ", ";
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
                this.overrideMethods(newClass.getMethods(), false, level);

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
        return result;
    }

    private void addReturnStatement(NodeList<Statement> statements, MethodDeclaration method, Statement current_statement,
                                    String class_level, String level) {

        statements.add(new ExpressionStmt(new AssignExpr(new NameExpr(method.getTypeAsString() + " return_statement" + this.count_return_statement),
                current_statement.asReturnStmt().getExpression().get(), ASSIGN)));

        String expression = level.equals(this.lattice.getTop()) ? "new " + method.getTypeAsString() + "("
                : "new " + method.getTypeAsString() + "_" + level + "(";

        for (BodyDeclaration<?> field : this.custom_classes.get(method.getTypeAsString())) {
            expression = expression + "return_statement" + this.count_return_statement + "." + field.toFieldDeclaration().get().getVariables().get(0).toString() + ", ";
        }
        this.count_return_statement = this.count_return_statement + 1;
        expression = expression.substring(0, expression.length() - 2);
        expression = expression + ")";

        statements.add(new ReturnStmt(expression));
    }

    private void changeIfStatement(MethodDeclaration method, IfStmt if_statement, String class_level, String level) {

        NodeList<Statement> statements = new NodeList<>();

        if (if_statement.hasThenBlock()) {
            this.compute_statements(statements, if_statement.getThenStmt().asBlockStmt(),
                    method, class_level, level);

            BlockStmt newThenBlock = new BlockStmt();
            newThenBlock.copyStatements(statements);
            if_statement.setThenStmt(newThenBlock);
        }

        if (if_statement.hasElseBlock()) {
            this.compute_statements(statements, if_statement.getElseStmt().get().asBlockStmt(),
                    method, class_level, level);

            BlockStmt newElseBlock = new BlockStmt();
            newElseBlock.copyStatements(statements);
            if_statement.setElseStmt(newElseBlock);
        }
    }

    public void compute_statements(NodeList<Statement> statements, BlockStmt body, MethodDeclaration method, String class_level, String level) {
        for (Statement statement : body.getStatements()) {
            if (statement.isReturnStmt()) {
                addReturnStatement(statements, method, statement, class_level, level);
            } else {
                if (statement.isIfStmt()) {
                    changeIfStatement(method, statement.asIfStmt(), class_level, level);
                }
                statements.add(statement);
            }
        }
    }

    private void createMathExpression(NodeList<Expression> arguments, MethodCallExpr mathExpr, int index, String min_max) {
        MethodCallExpr newExpr;
        if (index + 1 < arguments.size()) {
            newExpr = new MethodCallExpr();
            newExpr.setScope(new NameExpr("Math"));
            newExpr.setName(min_max);
            newExpr.addArgument(new NameExpr(arguments.get(index).toString()));
            mathExpr.addArgument(newExpr);
            createMathExpression(arguments, newExpr, index + 1, min_max);
        } else {
            mathExpr.addArgument(new NameExpr(arguments.get(index).toString()));
        }
    }

    private void addOverride(String class_level, MethodDeclaration method) {
        BlockStmt body = method.getBody().get();
        SwitchStmt sw = new SwitchStmt();
        NodeList<SwitchEntry> entrys = new NodeList<>();

        NodeList<Expression> s_selector_arguments = new NodeList<>();
        s_selector_arguments.add(new NameExpr("this.level()"));
        for (Parameter par : method.getParameters()) {
            s_selector_arguments.add(new NameExpr(par.getNameAsString() + ".level()"));
        }

        MethodCallExpr mathExpr = new MethodCallExpr();
        mathExpr.setScope(new NameExpr("Math"));
        if (this.combination.equals("meet")) {
            mathExpr.setName("min");
            mathExpr.addArgument(new NameExpr(s_selector_arguments.get(0).toString()));
            createMathExpression(s_selector_arguments, mathExpr, 1, "min");
        } else if (this.combination.equals("join")) {
            mathExpr.setName("max");
            mathExpr.addArgument(new NameExpr(s_selector_arguments.get(0).toString()));
            createMathExpression(s_selector_arguments, mathExpr, 1, "max");
            sw.setSelector(mathExpr);
        }
        sw.setSelector(mathExpr);

        for (String level : this.lattice.getMatrix().keySet()) {
            NodeList<Statement> statements = new NodeList<>();
            BlockStmt newBody = new BlockStmt();
            newBody.copyStatements(body);

            if (!level.equals(class_level)) {
                this.compute_statements(statements, newBody, method, class_level, level);
                entrys.add(new SwitchEntry(new NodeList<>(new IntegerLiteralExpr(this.lattice.getLevelDepht().get(level)))
                        , SwitchEntry.Type.STATEMENT_GROUP, statements));
            }
        }
        NodeList<Statement> statements = new NodeList<>();
        BlockStmt newBody = new BlockStmt();
        newBody.copyStatements(body);
        this.compute_statements(statements, newBody,  method, class_level, class_level);
        entrys.add(new SwitchEntry(new NodeList<>(), SwitchEntry.Type.STATEMENT_GROUP , statements));

        sw.setEntries(entrys);
        method.setBody(new BlockStmt(new NodeList<Statement>(sw)));
    }

    private void overrideMethods(List<MethodDeclaration> methods, boolean top_class, String level) {
        if (top_class == false) {
            for (MethodDeclaration method : methods) {
                method.addAnnotation("Override");
                if (!method.getNameAsString().equals("level") && !method.getType().isVoidType())
                    addOverride(level, method);
            }
        } else {
            for (MethodDeclaration method : methods) {
                if (!method.getNameAsString().equals("level") && !method.getType().isVoidType())
                    addOverride(level, method);
            }
        }
    }

    public String toString() {
        /*YamlPrinter printer = new YamlPrinter(true);
        return printer.output(cu);*/
        return cu.toString();
    }
}

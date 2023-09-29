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
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.javaparser.ast.expr.AssignExpr.Operator.ASSIGN;

public class ASTParser extends ModifierVisitor<Void> {
    private Lattice lattice;
    public CompilationUnit cu;
    private String combination;
    private String main_class = "";
    private HashMap<String, List<BodyDeclaration<?>>> custom_classes = new HashMap<>();
    private HashMap<String, String> variable_level = new HashMap<>();
    private HashMap<String, String> declassification_variables = new HashMap<>();
    private Stack<String> declassification_stack = new Stack<>();
    private Integer count_declassification;
    private Integer count_return_statement = 0;
    private String current_class = "";


    public ASTParser(Lattice l, String filename) {
        this.lattice = l;
        this.combination = l.getCombination();
        this.count_declassification = 0;

        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(Main.class).resolve(filename.substring(0, filename.lastIndexOf("/") + 1)));
        this.cu = sourceRoot.parse("", filename.substring(filename.lastIndexOf("/") + 1));

        List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class).stream().collect(Collectors.toList());
        List<MethodDeclaration> methods;
        for (ClassOrInterfaceDeclaration c : classes) {
            methods = c.getMethods();
            for (MethodDeclaration m : methods) {
                if (m.getNameAsString().equals("main")) {
                    main_class = c.getNameAsString();
                    System.out.println(c.getNameAsString());
                }
            }
            if (!c.getNameAsString().equals(main_class)) {
                List<BodyDeclaration<?>> fields = c.getMembers().stream().filter(n -> n.isFieldDeclaration()).collect(Collectors.toList());
                this.custom_classes.put(c.getNameAsString(), fields);
            }
        }

        this.visit(cu, null);
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
                this.variable_level.put(variable + "_" + this.count_declassification, last_level.substring(0, last_level.lastIndexOf("_")) + "_" + new_level);
            } else {
                custom_class = last_level;
                this.variable_level.put(variable + "_" + this.count_declassification, last_level + "_" + new_level);
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
        }

        super.visit(c, arg);
        return c;
    }

    @Override
    public Visitable visit(AssignExpr a, Void arg) {
        if (a.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString().equals(main_class)) {
            this.assignmentExprRewrite(a);
        }
        super.visit(a, arg);
        return a;
    }

    public Visitable visit(NameExpr a, Void arg) {
        if(a.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            if (a.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString().equals(main_class)) {
                changeNameExprDeclassification(a);
            }
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

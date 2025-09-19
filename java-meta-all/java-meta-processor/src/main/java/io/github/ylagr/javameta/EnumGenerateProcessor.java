package io.github.ylagr.javameta;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import io.github.ylagr.javameta.JavacContextHolder.*;

import static io.github.ylagr.javameta.Hider.capitalize;
import static io.github.ylagr.javameta.JavacContextHolder.*;
import static io.github.ylagr.javameta.JavacContextHolder.names;
import static io.github.ylagr.javameta.JavacContextHolder.treeMaker;

/**
 * @author suiwp
 * @date 2025/9/19 11:04
 */
public class EnumGenerateProcessor extends AbstractProcessor {
    public EnumGenerateProcessor() {
    }
    static {
        JdkAdapter.invoke();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        JavacContextHolder.init(processingEnv);
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }
        // 遍历所有被 @PrintHello 注解的元素
        for (Element element : roundEnv.getElementsAnnotatedWith(Meta.class)) {
            // 确保注解在类上
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                JCTree tree = (JCTree) trees.getTree(element);

                if (tree != null) {
                    // 创建一个新的 TreeTranslator 来修改 AST
                    tree.accept(new TreeTranslator() {
                        @Override
                        public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                            // 通过类名和结构特征判断是否为生成的类
                            generateMeta_staticMethod(jcClassDecl);
                        }

                        private void generateMeta_staticMethod(JCTree.JCClassDecl jcClassDecl) {
                            // 创建静态方法修饰符 (public static)
                            JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC);

                            // 指定返回类型（示例中使用 String 类型）
                            JCTree.JCExpression returnType = treeMaker.Ident(names.fromString("String"));
                            // 或者使用其他类型，例如:
                            // JCTree.JCExpression returnType = treeMaker.TypeIdent(com.sun.tools.javac.code.TypeTag.INT);

                            // 方法参数列表（空参数）
                            List<JCTree.JCVariableDecl> parameters = List.nil();

                            // 创建方法体: return "Some Value";
                            JCTree.JCLiteral returnValue = treeMaker.Literal("Generated return value");
                            JCTree.JCReturn returnStatement = treeMaker.Return(returnValue);
                            JCTree.JCBlock methodBody = treeMaker.Block(0, List.of(returnStatement));

                            // 创建方法声明
                            JCTree.JCMethodDecl staticMethod = treeMaker.MethodDef(
                                    modifiers,
                                    names.fromString("getGeneratedValue"), // 方法名
                                    returnType, // 返回类型
                                    List.nil(), // 类型参数
                                    parameters, // 参数列表
                                    List.nil(), // 异常列表
                                    methodBody, // 方法体
                                    null // 默认值
                            );

                            // 将新方法添加到类的成员列表中
                            jcClassDecl.defs = jcClassDecl.defs.append(staticMethod);
                        }

                        private void generateMeta_memberMethod(JCTree.JCClassDecl jcClassDecl) {

                            // 创建 sayHello 方法的参数列表 (空)
                            List<JCTree.JCVariableDecl> parameters = List.nil();

                            // 创建方法体: { System.out.println("Hello from " + this.getClass().getSimpleName()); }
                            // 创建 System.out
                            JCTree.JCFieldAccess systemOut = treeMaker.Select(
                                    treeMaker.Ident(names.fromString("System")),
                                    names.fromString("out")
                            );

                            // 创建字符串字面量 "Hello from "
                            JCTree.JCLiteral helloLiteral = treeMaker.Literal("Hello from ");

                            // 创建 this.getClass().getSimpleName() 表达式
                            JCTree.JCMethodInvocation getClassCall = treeMaker.Apply(
                                    List.nil(),
                                    treeMaker.Select(
                                            treeMaker.Ident(names.fromString("this")),
                                            names.fromString("getClass")
                                    ),
                                    List.nil()
                            );

                            JCTree.JCMethodInvocation getSimpleNameCall = treeMaker.Apply(
                                    List.nil(),
                                    treeMaker.Select(getClassCall, names.fromString("getSimpleName")),
                                    List.nil()
                            );

                            // 拼接字符串: "Hello from " + this.getClass().getSimpleName()
                            JCTree.JCBinary concatExpr = treeMaker.Binary(
                                    JCTree.Tag.PLUS,
                                    helloLiteral,
                                    getSimpleNameCall
                            );

                            // 创建方法调用: System.out.println(...)
                            JCTree.JCMethodInvocation printCall = treeMaker.Apply(
                                    List.nil(),
                                    treeMaker.Select(systemOut, names.fromString("println")),
                                    List.of(concatExpr)
                            );

                            // 创建 return 语句 (void 方法不需要返回值，但这里为了完整性可以省略)
                            // JCTree.JCReturn returnStmt = treeMaker.Return(null); // 对于 void 方法，通常不需要显式 return

                            // 创建方法体块
                            JCTree.JCBlock methodBody = treeMaker.Block(0, List.of(
                                    treeMaker.Exec(printCall) // 将方法调用包装成语句
                                    // 如果需要 return 语句，可以添加: , returnStmt
                            ));

                            // 创建方法修饰符 (public)
                            JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC);

                            // 创建方法声明
                            JCTree.JCMethodDecl sayHelloMethod = treeMaker.MethodDef(
                                    modifiers,
                                    names.fromString("sayHello"), // 方法名
                                    treeMaker.TypeIdent(com.sun.tools.javac.code.TypeTag.VOID), // 返回类型 void
                                    List.nil(), // 类型参数
                                    parameters, // 参数列表
                                    List.nil(), // 异常列表
                                    methodBody, // 方法体
                                    null // 默认值 (对于方法为 null)
                            );

                            // 将新方法添加到类的成员列表中
                            jcClassDecl.defs = jcClassDecl.defs.append(sayHelloMethod);

                        }


                    });
                }
            }
        }
        return true; // 告诉编译器该注解已被处理
    }
}

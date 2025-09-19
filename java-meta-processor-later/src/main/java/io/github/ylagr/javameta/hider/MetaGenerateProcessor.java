package io.github.ylagr.javameta.hider;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import io.github.ylagr.javameta.Meta;
import sun.misc.Unsafe;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * @author suiwp
 * @date 2025/9/19 10:49
 */ //    @SupportedAnnotationTypes({"io.github.ylagr.javameta.Meta"}) // 指定要处理的注解
//    @SupportedSourceVersion(SourceVersion.RELEASE_8) // 支持的Java版本
public class MetaGenerateProcessor extends AbstractProcessor {
    public MetaGenerateProcessor() {
    }

    static {
        JDK.breakSecurity();
    }

    private Trees trees;
    private TreeMaker treeMaker;
    private Names names;

    private void disableJava9SillyWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get((Object) null);
            Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), (Object) null);
        } catch (Throwable var5) {
        }

    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        disableJava9SillyWarning();
        super.init(processingEnv);
        Class<? extends ProcessingEnvironment> processingEnvClass = processingEnv.getClass();
        //        Class<?>[] interfaces = processingEnvClass.getInterfaces();
        //  javax.annotation.processing.ProcessingEnvironment
        //  org.jetbrains.jps.javac.APIWrappers$WrapperDelegateAccessor
        //        for (Class<?> anInterface : interfaces) {
        //            System.err.println(anInterface.getName());
        //        }

        final Context context;

        if (processingEnv instanceof JavacProcessingEnvironment) {
            context = ((JavacProcessingEnvironment) processingEnv).getContext();
        } else if (Proxy.isProxyClass(processingEnvClass)) {
            try {
                Method getWrapperDelegate = processingEnvClass.getDeclaredMethod("getWrapperDelegate");
                Object invoke = getWrapperDelegate.invoke(processingEnv);
                if (invoke instanceof JavacProcessingEnvironment) {
                    processingEnv = (JavacProcessingEnvironment) invoke;
                    context = ((JavacProcessingEnvironment) invoke).getContext();
                } else {
                    throw new IllegalStateException("Not support ProcessingEnvironment: " + processingEnvClass);
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new IllegalStateException("Not support ProcessingEnvironment: " + processingEnvClass, e);
            }
        } else {
            throw new IllegalStateException("Not support ProcessingEnvironment: " + processingEnvClass);
        }

        this.trees = Trees.instance(processingEnv);
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new HashSet<>();
        supportedAnnotationTypes.add(Meta.class.getCanonicalName());
        return supportedAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
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
                            if (isMetaClass(jcClassDecl)) {
                                super.visitClassDef(jcClassDecl);
                                return;
                            }
                            // 检查是否已经包含meta类
                            if (!hasClassMetaClass(jcClassDecl)) {
                                //                                    generateMeta_memberMethod(jcClassDecl);
                                //                                    generateMeta_staticMethod(jcClassDecl);
                                generateCompleteMetaClass(jcClassDecl);
                                // 调用父类方法完成访问
                                super.visitClassDef(jcClassDecl);
                            }
                        }

                        private boolean isMetaClass(JCTree.JCClassDecl jcClassDecl) {
                            String className = jcClassDecl.name.toString();

                            // 1. 直接检查类名是否为"meta"
                            if ("meta".equals(className)) {
                                return true;
                            }

                            // 2. 检查是否具有元信息类的典型结构特征
                            return hasGeneratedMarkerField(jcClassDecl);
                        }

                        // 简单的注释处理方法（兼容性更好）
                        private void addSimpleFieldMarker(JCTree.JCClassDecl jcClassDecl) {
                            // 虽然不能直接添加注释到AST，但可以记录生成的类信息
                            try {
                                // 可以通过其他方式标记，比如添加特殊的静态字段
                                JCTree.JCVariableDecl markerField = treeMaker.VarDef(
                                        treeMaker.Modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL),
                                        names.fromString("$__generated_by_meta_processor__"),
                                        treeMaker.Ident(names.fromString("Boolean")),
                                        treeMaker.Literal(TypeTag.BOT, null)
                                );

                                // 将标记字段添加到类中
                                jcClassDecl.defs = jcClassDecl.defs.prepend(markerField);
                            } catch (Exception e) {
                                // 如果添加标记字段失败，不影响主要功能
                            }
                        }

                        // 检查是否有标记字段
                        private boolean hasGeneratedMarkerField(JCTree.JCClassDecl jcClassDecl) {
                            for (JCTree def : jcClassDecl.defs) {
                                if (def instanceof JCTree.JCVariableDecl) {
                                    JCTree.JCVariableDecl varDecl = (JCTree.JCVariableDecl) def;
                                    if ("$__generated_by_meta_processor__".equals(varDecl.name.toString())) {
                                        return true;
                                    }
                                }
                            }
                            return false;
                        }

                        // 检查类是否实现了GeneratedMetaClass接口
                        private boolean implementsGeneratedMetaClassInterface(JCTree.JCClassDecl jcClassDecl) {
                            if (jcClassDecl.implementing != null) {
                                for (JCTree.JCExpression impl : jcClassDecl.implementing) {
                                    String interfaceName = getInterfaceName(impl);
                                    if (Meta.Inner.class.getCanonicalName().equals(interfaceName)) {
                                        return true;
                                    }
                                }
                            }
                            return false;
                        }

                        private String getInterfaceName(JCTree.JCExpression impl) {
                            if (impl instanceof JCTree.JCIdent) {
                                return ((JCTree.JCIdent) impl).name.toString();
                            } else if (impl instanceof JCTree.JCFieldAccess) {
                                return impl.toString();
                            }
                            return impl.toString();
                        }

                        private boolean hasClassMetaClass(JCTree.JCClassDecl jcClassDecl) {
                            // 检查类是否已经包含名为"meta"的内部类
                            for (JCTree def : jcClassDecl.defs) {
                                if (def instanceof JCTree.JCClassDecl) {
                                    JCTree.JCClassDecl innerClass = (JCTree.JCClassDecl) def;
                                    if ("meta".equals(innerClass.name.toString())) {
                                        System.out.println(">>>>>> WARNING : already have meta static sub class");
                                        return true;
                                    }
                                }
                            }
                            return false;
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
                                    treeMaker.TypeIdent(TypeTag.VOID), // 返回类型 void
                                    List.nil(), // 类型参数
                                    parameters, // 参数列表
                                    List.nil(), // 异常列表
                                    methodBody, // 方法体
                                    null // 默认值 (对于方法为 null)
                            );

                            // 将新方法添加到类的成员列表中
                            jcClassDecl.defs = jcClassDecl.defs.append(sayHelloMethod);

                        }

                        private void generateCompleteMetaClass(JCTree.JCClassDecl jcClassDecl) {
                            // 创建 meta 内部类
                            JCTree.JCClassDecl metaClass = treeMaker.ClassDef(
                                    treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC),
                                    names.fromString("meta"),
                                    List.nil(),
                                    null,
                                    List.nil(),
                                    generateMetaClassMembers(jcClassDecl)
                            );

                            addSimpleFieldMarker(jcClassDecl);
                            jcClassDecl.defs = jcClassDecl.defs.append(metaClass);
                        }

                        private List<JCTree> generateMetaClassMembers(JCTree.JCClassDecl outerClass) {
                            List<JCTree> members = List.nil();
                            Set<Name> processedFieldNames = new HashSet<>();
                            // 为每个字段创建对应的元信息内部类
                            for (JCTree def : outerClass.defs) {
                                if (def instanceof JCTree.JCVariableDecl) {
                                    JCTree.JCVariableDecl field = (JCTree.JCVariableDecl) def;
                                    // 避免重复处理
                                    if (!processedFieldNames.contains(field.name)) {
                                        JCTree.JCClassDecl fieldMetaClass = createFieldMetaClass(field);
                                        addSimpleFieldMarker(fieldMetaClass);
                                        members = members.append(fieldMetaClass);
                                        processedFieldNames.add(field.name);
                                    }
                                }
                            }

                            return members;
                        }

                        private JCTree.JCClassDecl createFieldMetaClass(JCTree.JCVariableDecl field) {
                            Name fieldName = field.name;
                            Name metaClassName = names.fromString(fieldName.toString());

                            // 创建字段元信息类的成员
                            List<JCTree> metaMembers = List.nil();

                            // name 常量
                            JCTree.JCVariableDecl nameConstant = treeMaker.VarDef(
                                    treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC | Flags.FINAL),
                                    names.fromString("named"),
                                    treeMaker.Ident(names.fromString("String")),
                                    treeMaker.Literal(fieldName.toString())
                            );
                            metaMembers = metaMembers.append(nameConstant);

                            // type 常量 (返回字段类型)
                            JCTree.JCVariableDecl typeConstant = createTypeConstant(field);
                            if (typeConstant != null) {
                                metaMembers = metaMembers.append(typeConstant);
                            }

                            // getter 方法名
                            JCTree.JCVariableDecl getterName = treeMaker.VarDef(
                                    treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC | Flags.FINAL),
                                    names.fromString("getter"),
                                    treeMaker.Ident(names.fromString("String")),
                                    treeMaker.Literal("get" + Hider.capitalize(fieldName.toString()))
                            );
                            metaMembers = metaMembers.append(getterName);

                            // setter 方法名
                            JCTree.JCVariableDecl setterName = treeMaker.VarDef(
                                    treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC | Flags.FINAL),
                                    names.fromString("setter"),
                                    treeMaker.Ident(names.fromString("String")),
                                    treeMaker.Literal("set" + Hider.capitalize(fieldName.toString()))
                            );
                            metaMembers = metaMembers.append(setterName);

                            // 创建元信息内部类
                            return treeMaker.ClassDef(
                                    treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC),
                                    metaClassName,
                                    List.nil(),
                                    null,
                                    List.nil(),
                                    metaMembers
                            );
                        }

                        private JCTree.JCVariableDecl createTypeConstant(JCTree.JCVariableDecl field) {
                            JCTree.JCExpression fieldType = field.vartype;

                            // 创建 Class<?> 类型
                            JCTree.JCExpression classType = treeMaker.TypeApply(
                                    treeMaker.Ident(names.fromString("Class")),
                                    List.of(field.vartype)
                                    //                                        List.of(treeMaker.Wildcard(treeMaker.TypeBoundKind(BoundKind.EXTENDS), field.vartype))
                            );

                            // 创建类型引用 (这里简化处理，实际需要根据具体类型处理)
                            JCTree.JCExpression typeValue;
                            if (fieldType instanceof JCTree.JCIdent) {
                                JCTree.JCIdent ident = (JCTree.JCIdent) fieldType;
                                try {
                                    // 尝试创建 class 引用
                                    typeValue = treeMaker.Select(
                                            treeMaker.Ident(ident.name),
                                            names.fromString("class")
                                    );
                                } catch (Exception e) {
                                    // 如果无法创建，返回 null
                                    return null;
                                }
                            } else {
                                return null;
                            }

                            return treeMaker.VarDef(
                                    treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC | Flags.FINAL),
                                    names.fromString("typed"),
                                    classType,
                                    typeValue
                            );
                        }

                        // 创建实现的接口列表
                        private List<JCTree.JCExpression> createImplementedInterfaces() {
                            // 创建对GeneratedMetaClass接口的引用
                            JCTree.JCExpression interfaceRef = treeMaker.Ident(names.fromString(Meta.Inner.class.getCanonicalName()));

                            // 如果需要使用全限定名
                            // JCTree.JCExpression interfaceRef = createFullyQualifiedInterfaceReference();

                            return List.of(interfaceRef);
                        }


                    });
                }
            }
        }
        return true; // 告诉编译器该注解已被处理
    }
}

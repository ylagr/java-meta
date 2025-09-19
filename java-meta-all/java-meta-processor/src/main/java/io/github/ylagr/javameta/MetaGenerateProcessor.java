package io.github.ylagr.javameta;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

import static io.github.ylagr.javameta.Hider.capitalize;
import static io.github.ylagr.javameta.JavacContextHolder.*;

//    @SupportedAnnotationTypes({"io.github.ylagr.javameta.Meta"}) // 指定要处理的注解
//    @SupportedSourceVersion(SourceVersion.RELEASE_8) // 支持的Java版本
public class MetaGenerateProcessor extends AbstractProcessor {
    public MetaGenerateProcessor() {
    }
    static {
        JdkAdapter.invoke();
    }

    @Override
    public synchronized void init(javax.annotation.processing.ProcessingEnvironment processingEnv) {
        JavacContextHolder.init(processingEnv);
        super.init(processingEnv);
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
                            com.sun.tools.javac.util.Name fieldName = field.name;
                            com.sun.tools.javac.util.Name metaClassName = names.fromString(fieldName.toString());

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
                                    treeMaker.Literal("get" + capitalize(fieldName.toString()))
                            );
                            metaMembers = metaMembers.append(getterName);

                            // setter 方法名
                            JCTree.JCVariableDecl setterName = treeMaker.VarDef(
                                    treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC | Flags.FINAL),
                                    names.fromString("setter"),
                                    treeMaker.Ident(names.fromString("String")),
                                    treeMaker.Literal("set" + capitalize(fieldName.toString()))
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
                                    List.of(createRecursiveType(fieldType))
//                                        List.of(treeMaker.Wildcard(treeMaker.TypeBoundKind(BoundKind.EXTENDS), field.vartype))
                            );

                            // 创建类型引用 (这里简化处理，实际需要根据具体类型处理)
                            JCTree.JCExpression typeValue;
                            typeValue = createTypeValue(fieldType);
                            if (typeValue == null) {
                                return null;
                            }
//                            if (fieldType instanceof JCTree.JCIdent) {
////                                JCTree.JCIdent ident = (JCTree.JCIdent) fieldType;
//                                try {
//                                    // 尝试创建 class 引用
////                                    typeValue = treeMaker.Select(
////                                            treeMaker.Ident(ident.name),
////                                            names.fromString("class")
////                                    );
//                                } catch (Exception e) {
//                                    // 如果无法创建，返回 null
//                                    return null;
//                                }
//                            } else {
//                                return null;
//                            }

                            return treeMaker.VarDef(
                                    treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC | Flags.FINAL),
                                    names.fromString("typed"),
                                    classType,
                                    typeValue
                            );
                        }

                        /**
                         * 递归处理泛型嵌套类型，构建正确的类型表达式
                         */
                        private JCTree.JCExpression createRecursiveType(JCTree.JCExpression type) {
                            if (type instanceof JCTree.JCTypeApply) {
                                // 处理泛型类型，如 List<String>, Map<K,V> 等
                                JCTree.JCTypeApply typeApply = (JCTree.JCTypeApply) type;

                                // 递归处理所有类型参数
                                List<JCTree.JCExpression> newArguments = List.nil();
                                for (JCTree.JCExpression arg : typeApply.arguments) {
                                    newArguments = newArguments.append(createRecursiveType(arg));
                                }

                                // 返回处理后的泛型类型
                                return treeMaker.TypeApply(
                                        createRecursiveType(typeApply.clazz),
                                        newArguments
                                );
                            } else if (type instanceof JCTree.JCArrayTypeTree) {
                                // 处理数组类型
                                JCTree.JCArrayTypeTree arrayType = (JCTree.JCArrayTypeTree) type;
                                return treeMaker.TypeArray(createRecursiveType(arrayType.elemtype));
                            } else if (type instanceof JCTree.JCWildcard) {
                                // 处理通配符类型如 ? extends SomeType
                                JCTree.JCWildcard wildcard = (JCTree.JCWildcard) type;
                                if (wildcard.inner != null) {
                                    return treeMaker.Wildcard(
                                            wildcard.kind,
                                            wildcard.inner
                                    );
                                }
                            }
                            // 对于简单类型（如 String, Integer 等），直接返回
                            return type;
                        }

                        /**
                         * 递归创建类型值表达式，用于 class 字面量
                         */
                        private JCTree.JCExpression createTypeValue(JCTree.JCExpression type) {
                            if (type instanceof JCTree.JCTypeApply) {
                                // 处理泛型类型，如 List<String> -> List.class
                                JCTree.JCTypeApply typeApply = (JCTree.JCTypeApply) type;
                                // 对于泛型，使用原始类型创建 class 引用
                                return createTypeValue(typeApply.clazz);
                            } else if (type instanceof JCTree.JCArrayTypeTree) {
                                // 处理数组类型
                                JCTree.JCArrayTypeTree arrayType = (JCTree.JCArrayTypeTree) type;
                                JCTree.JCExpression elemTypeValue = createTypeValue(arrayType.elemtype);
                                if (elemTypeValue != null) {
                                    return treeMaker.Select(elemTypeValue, names.fromString("class"));
                                }
                            } else if (type instanceof JCTree.JCIdent) {
                                // 处理标识符类型，如 String, Integer 等
                                JCTree.JCIdent ident = (JCTree.JCIdent) type;
                                try {
                                    return treeMaker.Select(
                                            treeMaker.Ident(ident.name),
                                            names.fromString("class")
                                    );
                                } catch (Exception e) {
                                    // 如果无法创建，返回 null
                                    return null;
                                }
                            } else if (type instanceof JCTree.JCFieldAccess) {
                                // 处理带包名的类型，如 java.lang.String
                                JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess) type;
                                return treeMaker.Select(type, names.fromString("class"));
                            }

                            return null;
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


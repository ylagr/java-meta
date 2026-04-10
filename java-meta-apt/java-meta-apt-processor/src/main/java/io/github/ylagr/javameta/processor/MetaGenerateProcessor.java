package io.github.ylagr.javameta.processor;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.ElementKind;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author suiwp
 */ //    @SupportedAnnotationTypes({"io.github.ylagr.javameta.annoation.Meta"}) // 指定要处理的注解
//    @SupportedSourceVersion(SourceVersion.RELEASE_8) // 支持的Java版本
public class MetaGenerateProcessor extends AbstractProcessor {
    public MetaGenerateProcessor() {
    }

    @Override
    public synchronized void init(javax.annotation.processing.ProcessingEnvironment processingEnv) {

        super.init(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new HashSet<>();
        supportedAnnotationTypes.add(io.github.ylagr.javameta.annoation.Meta.class.getCanonicalName());
        return supportedAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private static class MetaUseData {
        private TypeElement typeElement;
        private String packageName;
        private String className;
        private String metaClassName;
        public MetaUseData(TypeElement typeElement, String packageName, String className, String metaClassName) {
            this.typeElement = typeElement;
            this.packageName = packageName;
            this.className = className;
            this.metaClassName = metaClassName;
        }
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("MetaGenerateProcessor.process() called");
        if (roundEnv.processingOver()) {
            System.out.println("Processing over");
            return true;
        }
        System.out.println("Annotations: " + annotations);
        Set<MetaUseData> metaUseDataSet = new HashSet<>();
        // 遍历所有被 @Meta 注解的元素
        for (Element element : roundEnv.getElementsAnnotatedWith(io.github.ylagr.javameta.annoation.Meta.class)) {
            System.out.println("Found element: " + element);
            // 确保注解在类上
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                // 获取类的包名
                String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
                // 获取类名
                String className = typeElement.getSimpleName().toString();
                System.out.println("Processing class: " + packageName + "." + className);
                // 生成元数据类
                Optional<MetaUseData> metaUseData = generateMetaClass(typeElement, packageName, className);
                metaUseData.ifPresent(metaUseDataSet::add);
            }
        }

//        for (MetaUseData metaUseData : metaUseDataSet) {
//            generateMetaUtils( metaUseData.packageName, metaUseData.className, metaUseData.metaClassName);
//        }
        return true; // 告诉编译器该注解已被处理
    }

    private Optional<MetaUseData> generateMetaClass(TypeElement typeElement, String packageName, String className) {
        // 生成Meta类的源文件
        // 获取Meta注解的aliasName属性
        String metaClassName = className + "$Meta";
        io.github.ylagr.javameta.annoation.Meta metaAnnotation = typeElement.getAnnotation(io.github.ylagr.javameta.annoation.Meta.class);
        if (metaAnnotation != null && !metaAnnotation.aliasName().isEmpty()) {
            metaClassName = metaAnnotation.aliasName();
        }
        javax.tools.JavaFileObject javaFileObject;
        try {
            javaFileObject = processingEnv.getFiler().createSourceFile(packageName + "." + metaClassName);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }

        // 生成类内容
        StringBuilder classContent = new StringBuilder();
        // 包声明
        if (!packageName.isEmpty()) {
            classContent.append("package " + packageName + ";\n\n");
        }

        // 导入语句
        classContent.append("import io.github.ylagr.javameta.data.MetaData;\n");
        classContent.append("import java.util.List;\n\n");

        // 类声明
        classContent.append("/**\n");
        classContent.append(" * @author suiwp\n");
        classContent.append(" * @date 2026/4/10 14:16\n");
        classContent.append(" */\n");
        classContent.append("public class " + metaClassName + " {\n");

        // 生成静态实例
        classContent.append("    public static final " + metaClassName + " INSTANCE = new " + metaClassName + "();\n");

        // 为每个字段生成MetaData对象
        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement instanceof VariableElement && 
                enclosedElement.getKind() == ElementKind.FIELD) {
                VariableElement field = (VariableElement) enclosedElement;
                String fieldName = field.getSimpleName().toString();
                String fieldType = field.asType().toString();

                // 处理泛型类型
                String genericType = fieldType;
                if (fieldType.contains("<")) {
                    genericType = fieldType.substring(0, fieldType.indexOf("<"));
                }

                // 处理基础类型，转换为包装类
                fieldType = getWrapperClass(fieldType);
                
                // 生成MetaData字段
                // 添加原字段的位置信息，方便在IDE中跳转
                classContent.append("    /**\n");
                classContent.append("     * Link to field: " + fieldName + " in " + className + "\n");
                classContent.append("     * @see " + packageName + "." + className + "#" + fieldName + "\n");
                classContent.append("     */\n");
                classContent.append("    public final MetaData<" + fieldType + "> " + fieldName + " = new MetaData<>(\"" + fieldName + "\", " + genericType + ".class);\n");
            }
        }

        // 结束类声明
        classContent.append("}\n");

        // 写入文件
        try {
            java.io.Writer writer = javaFileObject.openWriter();
            writer.write(classContent.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.of(new MetaUseData(typeElement, packageName, className, metaClassName));

    }

    private void generateMetaUtils(String packageName, String className, String metaClassName) {
        // 生成MetaUtils类的源文件
        String utilsClassName = "MetaUtils";
        javax.tools.JavaFileObject javaFileObject;
        try {
            javaFileObject = processingEnv.getFiler().createSourceFile(packageName + "." + utilsClassName);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 生成类内容
        StringBuilder classContent = new StringBuilder();
        // 包声明
        if (!packageName.isEmpty()) {
            classContent.append("package " + packageName + ";\n\n");
        }

        // 导入语句
        classContent.append("import java.util.HashMap;\n");
        classContent.append("import java.util.Map;\n\n");

        // 类声明
        classContent.append("public class " + utilsClassName + " {\n");
        classContent.append("    private static final Map<Class<?>, Object> META_INSTANCES = new HashMap<>();\n\n");

        // 静态初始化块
        classContent.append("    static {\n");
        classContent.append("        // 注册元数据实例\n");
        classContent.append("        META_INSTANCES.put(" + className + ".class, " + metaClassName + ".INSTANCE);\n");
        classContent.append("    }\n\n");

        // 获取元数据实例的方法
        classContent.append("    public static <T> Object getMeta(Class<T> clazz) {\n");
        classContent.append("        return META_INSTANCES.get(clazz);\n");
        classContent.append("    }\n\n");

        // 获取字段类型的方法
        classContent.append("    public static Class<?> getFieldType(Class<?> clazz, String fieldName) {\n");
        classContent.append("        Object meta = getMeta(clazz);\n");
        classContent.append("        if (meta != null) {\n");
        classContent.append("            try {\n");
        classContent.append("                return (Class<?>) meta.getClass().getMethod(\"getFieldType\", String.class).invoke(meta, fieldName);\n");
        classContent.append("            } catch (Exception e) {\n");
        classContent.append("                e.printStackTrace();\n");
        classContent.append("            }\n");
        classContent.append("        }\n");
        classContent.append("        return null;\n");
        classContent.append("    }\n");

        // 结束类声明
        classContent.append("}\n");

        // 写入文件
        try {
            java.io.Writer writer = javaFileObject.openWriter();
            writer.write(classContent.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将基础类型转换为对应的包装类
     * @param type 基础类型
     * @return 包装类
     */
    private String getWrapperClass(String type) {
        switch (type) {
            case "byte":
                return "Byte";
            case "short":
                return "Short";
            case "int":
                return "Integer";
            case "long":
                return "Long";
            case "float":
                return "Float";
            case "double":
                return "Double";
            case "char":
                return "Character";
            case "boolean":
                return "Boolean";
            default:
                return type;
        }
    }

    private void generateOriginalClass(TypeElement typeElement, String packageName, String className) {
        // 创建Java文件对象
        javax.tools.JavaFileObject javaFileObject;
        try {
            javaFileObject = processingEnv.getFiler().createSourceFile(packageName + "." + className);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 生成类内容
        StringBuilder classContent = new StringBuilder();
        // 包声明
        if (!packageName.isEmpty()) {
            classContent.append("package " + packageName + ";\n\n");
        }

        // 导入语句
        // 注意：实际实现中需要分析原类的导入语句并复制

        // 类声明
        classContent.append("public class " + className + " {\n");


        // 生成原类的所有字段
        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement instanceof VariableElement && 
                enclosedElement.getKind() == ElementKind.FIELD) {
                VariableElement field = (VariableElement) enclosedElement;
                // 生成字段声明
                classContent.append("    " + field.getModifiers().toString().replaceAll("\\[|\\]", "") + " " + field.asType() + " " + field.getSimpleName() + ";\n");

            }
        }

        // 生成原类的所有方法
        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement instanceof ExecutableElement && 
                enclosedElement.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) enclosedElement;
                // 生成方法声明
                classContent.append("    " + method.getModifiers().toString().replaceAll("\\[|\\]", "") + " " + method.getReturnType() + " " + method.getSimpleName() + "(");

                // 生成方法参数
                java.util.List<? extends VariableElement> parameters = method.getParameters();
                for (int i = 0; i < parameters.size(); i++) {
                    VariableElement parameter = parameters.get(i);
                    classContent.append(parameter.asType() + " " + parameter.getSimpleName());
                    if (i < parameters.size() - 1) {
                        classContent.append(", ");
                    }
                }
                classContent.append(") {\n");
                // 方法体（这里简化处理，实际需要更复杂的逻辑）
                classContent.append("        // 方法体\n");
                classContent.append("    }\n\n");
            }
        }

        // 结束类声明
        classContent.append("}\n");

        // 写入文件
        try {
            java.io.Writer writer = javaFileObject.openWriter();
            writer.write(classContent.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
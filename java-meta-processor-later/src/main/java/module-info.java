module io.github.ylagr.javameta {
    requires java.compiler;
    requires jdk.compiler;
    requires jdk.unsupported;

    requires java.meta.processor;
//    exports java.meta.processor;
//    opens java.meta.processor;

//    provides javax.annotation.processing.Processor  with java.meta.processor.MetaGenerateProcessor ;
}


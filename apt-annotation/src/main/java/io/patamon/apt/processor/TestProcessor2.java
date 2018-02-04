package io.patamon.apt.processor;

import io.patamon.apt.annotation.TestAnnotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Desc: {@link TestAnnotation} 的processor
 *  当前processor的注册方式为:
 *      * 在resources/META-INF/services/javax.annotation.processing.Processor 文件中进行服务注册
 *      * 后续会采用谷歌的auto-service: https://github.com/google/auto
 *
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2018/1/30
 */
@SupportedAnnotationTypes({"io.patamon.apt.annotation.TestAnnotation2"})
// @SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TestProcessor2 extends AbstractProcessor {

    private Filer filer;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
    }

    /**
     * {@inheritDoc}
     *
     * @param annotations
     * @param roundEnv
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement element : annotations) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, ">>>>" + element.getSimpleName());
            System.out.println(">>>>>>>" + element.getSimpleName());
            roundEnv.getElementsAnnotatedWith(element).forEach(it -> {
                generateFile("io.patamon.apt.Test");
            });
        }
        return false;
    }


    private void generateFile(String path) {
        BufferedWriter writer = null;
        try {
            JavaFileObject sourceFile = filer.createSourceFile(path);
            int period = path.lastIndexOf('.');
            String myPackage = period > 0 ? path.substring(0, period) : null;
            String clazz = path.substring(period + 1);
            writer = new BufferedWriter(sourceFile.openWriter());
            if (myPackage != null) {
                writer.write("package " + myPackage + ";\n\n");
            }
            writer.write("import java.util.ArrayList;\n");
            writer.write("import java.util.List;\n\n");
            writer.write("/** This class is generated by CustomProcessor, do not edit. */\n");
            writer.write("public class " + clazz + " {\n");
            writer.write("    private static final List<String> ANNOTATIONS;\n\n");
            writer.write("    static {\n");
            writer.write("        ANNOTATIONS = new ArrayList<>();\n\n");
            writeMethodLines(writer);
            writer.write("    }\n\n");
            writer.write("    public static List<String> getAnnotations() {\n");
            writer.write("        return ANNOTATIONS;\n");
            writer.write("    }\n\n");
            writer.write("}\n");
        } catch (IOException e) {
            throw new RuntimeException("Could not write source for " + path, e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    //Silent
                }
            }
        }
    }

    private void writeMethodLines(BufferedWriter writer) throws IOException {
        for (int i = 0; i < 10; i++) {
            writer.write("        ANNOTATIONS.add(\"" + i + "\");\n");
        }
    }

    /**
     * If the processor class is annotated with {@link
     * SupportedSourceVersion}, return the source version in the
     * annotation.  If the class is not so annotated, {@link
     * SourceVersion#RELEASE_6} is returned.
     *
     * @return the latest source version supported by this processor
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}

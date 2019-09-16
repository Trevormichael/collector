package collector.lib;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@SuppressWarnings("WeakerAccess")
public class ProcessorUtils {
    private ProcessorUtils() {
    }

    public static Set<TypeElement> getTypeElementsToProcess(Set<? extends Element> elements,
                                                            Set<? extends Element> supportedAnnotations) {
        Set<TypeElement> typeElements = new HashSet<>();
        for (Element e : elements) {
            if (e instanceof TypeElement) {
                boolean found = false;
                for (Element sub : e.getEnclosedElements()) {
                    for (AnnotationMirror mirror : sub.getAnnotationMirrors()) {
                        for (Element annotation : supportedAnnotations) {
                            if (mirror.getAnnotationType().asElement().equals(annotation)) {
                                typeElements.add((TypeElement) e);
                                found = true;
                                break;
                            }
                        }
                        if (found) break;
                    }
                    if (found) break;
                }
            }
        }
        return typeElements;
    }

    public static MethodSpec getListBuilder(ClassName typeClassName, Element element, List<Element> toAdd) {
        String toAddString = Utils.joinToString(toAdd, (t) ->
                CollectorConfig.PARAMETER_INSTANCE + "." + t.getSimpleName().toString(), ", ");

        CodeBlock buildStatement = CodeBlock.builder()
                .add("return $N.asList(" + toAddString + ")", CollectorConfig.JAVA_UTIL_ARRAYS)
                .build();

        return MethodSpec.methodBuilder(CollectorConfig.getBuilderName(element))
                .addParameter(typeClassName, CollectorConfig.PARAMETER_INSTANCE)
                .returns(ClassName.get(List.class))
                .addStatement(buildStatement)
                .addModifiers(Modifier.PUBLIC)
                .build();
    }

}

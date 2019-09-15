package collector.lib;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class CollectorProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (!roundEnv.processingOver()) {

            Set<TypeElement> elementsToProcess = ProcessorUtils.getTypeElementsToProcess(roundEnv.getRootElements(), annotations);

            for (TypeElement typeElement : elementsToProcess) {

                String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
                String typeName = typeElement.getSimpleName().toString();

                ClassName typeClassName = ClassName.get(packageName, typeName);

                TypeSpec.Builder classBuilder = TypeSpec.classBuilder(typeElement.getSimpleName() + CollectorConfig.COLLECTOR_CLASS_SUFFIX)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Keep.class);

                MethodSpec.Builder bindBuilder = MethodSpec.methodBuilder(CollectorConfig.METHOD_BIND)
                        .addParameter(typeClassName, CollectorConfig.PARAMETER_INSTANCE)
                        .addModifiers(Modifier.PUBLIC);

                Map<String, List<Element>> elementTagMap = new HashMap<>();

                for (Element element : typeElement.getEnclosedElements()) {
                    Collect collect = element.getAnnotation(Collect.class);
                    if (collect != null) {
                        if (!elementTagMap.containsKey(collect.tag())) {
                            ArrayList<Element> elements = new ArrayList<>();
                            elements.add(element);
                            elementTagMap.put(collect.tag(), elements);
                        } else {
                            elementTagMap.get(collect.tag()).add(element);
                        }
                    }
                }

                for (Element element : typeElement.getEnclosedElements()) {
                    Collection collection = element.getAnnotation(Collection.class);
                    if (collection != null) {
                        Element e = typeUtils.asElement(element.asType());
                        ClassName listClass = ClassName.get(List.class);
                        if (e instanceof TypeElement) {
                            if (listClass.equals(ClassName.get((TypeElement) e))) {
                                String builderName = CollectorConfig.getBuilderName(element);
                                List<Element> elementsToAdd = elementTagMap.getOrDefault(collection.tag(), new ArrayList<>());
                                classBuilder.addMethod(ProcessorUtils.getListBuilder(typeClassName, element, elementsToAdd));
                                bindBuilder.addStatement("$N.$N = $N($N)",
                                        CollectorConfig.PARAMETER_INSTANCE,
                                        element.getSimpleName().toString(),
                                        builderName,
                                        CollectorConfig.PARAMETER_INSTANCE);
                            }
                        }
                    }
                }

                classBuilder.addMethod(bindBuilder.build());

                classBuilder.addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .build());

                try {
                    JavaFile.builder("collector.tmc",
                            classBuilder.build())
                            .build()
                            .writeTo(filer);
                } catch (IOException e) {
                    messager.printMessage(Diagnostic.Kind.ERROR, e.toString());
                }
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new TreeSet<>(Arrays.asList(
                Collect.class.getCanonicalName(),
                Collection.class.getCanonicalName()));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}

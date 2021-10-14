package com.anyu.antask.auto;

import com.anyu.antask.common.stereotype.SourceService;
import com.anyu.antask.common.stereotype.SourceServiceScan;
import com.anyu.antask.util.CollUtil;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 扫描 {@link SourceService} 标注的类
 *
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/26
 */
public class SourceServiceScanner {

    /**
     * 扫描包下的{@link SourceService}标识的服务类
     *
     * @return {@link SourceService}标识的服务类
     */
    public List<Class<?>> scanSourceServiceClassOfBasePackage() {
        final URL src = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(""));
        final String classPath = new File(src.getPath()).getPath();
        //搜寻SourceScan
        final String basePackage = getBasePackage(classPath);
        final String basePackageUri = basePackage.replace(".", File.separator);
        final String targetUrl = classPath + basePackageUri;
        final List<String> classUrls = new ArrayList<>();
        //找到包下所有类的URL
        findAllClassUrls(new File(targetUrl), classUrls);
        //URL转换成类信息
        final List<Class<?>> classes = classUrls.stream()
                .map(url -> parseClassPath2Class(url, classPath))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return listSourceServiceClasses(classes);
    }

    private String getBasePackage(String classPath) {
        final List<Class<?>> scanAnnotationClass = new ArrayList<>(1);
        searchSourceScanAnnotation(new File(classPath), new File(classPath).getPath(), scanAnnotationClass);
        if (CollUtil.isNotEmpty(scanAnnotationClass)) {
            final SourceServiceScan sourceServiceScan = scanAnnotationClass.get(0)
                    .getDeclaredAnnotation(SourceServiceScan.class);
            if (sourceServiceScan != null)
                return sourceServiceScan.basePackage();
        }
        throw new RuntimeException("未发现@SourceServiceScan");
    }

    private void searchSourceScanAnnotation(File file, String classPath, List<Class<?>> scanAnnotation) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null)
                return;
            if (files.length == 1)
                searchSourceScanAnnotation(files[0], classPath, scanAnnotation);
            for (File f : files) {
                if (f.isDirectory())
                    continue;
                final Class<?> clazz = parseClassPath2Class(f.getPath(), classPath + File.separator);
                if (clazz != null)
                    scanAnnotation.add(clazz);
            }
        }
    }

    /**
     * 查找到被 {@link SourceService}标识的服务类
     *
     * @param classes 包下的所有类
     * @return 被 {@link SourceService}标识的服务类
     */
    private List<Class<?>> listSourceServiceClasses(List<Class<?>> classes) {
        final List<Class<?>> serviceClasses = new ArrayList<>();
        for (Class<?> clazz : classes) {
            final SourceService sourceService = clazz.getDeclaredAnnotation(SourceService.class);
            if (sourceService != null)
                serviceClasses.add(clazz);
        }
        return serviceClasses;
    }

    /**
     * 将类URL装换成类信息
     *
     * @param classUrl  类URL
     * @param classPath classpath
     * @return 类信息
     */
    private Class<?> parseClassPath2Class(String classUrl, String classPath) {
        String classRef = classUrl.replace(classPath, "")
                .replace(File.separator, ".")
                .replace(".class", "");
        try {
            return Class.forName(classRef);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void findAllClassUrls(File file, List<String> classUrls) {
        if (!file.isDirectory() && file.getName().endsWith(".class"))
            classUrls.add(file.getPath());
        final File[] files = file.listFiles();
        if (files == null)
            return;
        for (File f : files)
            findAllClassUrls(f, classUrls);
    }

}

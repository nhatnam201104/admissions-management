package com.example.managementadmissionwf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map Excel columns to entity/DTO fields.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {
    /**
     * The name of the column in the Excel header.
     */
    String name();

    /**
     * The zero-based index of the column in the Excel file.
     * Default is -1, which means order depends on field definition or header name
     * mapping.
     */
    int index() default -1;
}

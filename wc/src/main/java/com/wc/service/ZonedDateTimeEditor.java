package com.wc.service;
//new add

import java.beans.PropertyEditorSupport;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ZonedDateTimeEditor extends PropertyEditorSupport {
    private final DateTimeFormatter formatter;

    public ZonedDateTimeEditor(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            setValue(ZonedDateTime.parse(text, formatter));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use the correct format.");
        }
    }

    @Override
    public String getAsText() {
        return formatter.format((ZonedDateTime) getValue());
    }
}

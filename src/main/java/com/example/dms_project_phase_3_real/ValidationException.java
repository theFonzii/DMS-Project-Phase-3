package com.example.dms_project_phase_3_real;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * ValidationException.java
 * This checked exception is used whenever user input fails validation.
 */

public class ValidationException extends Exception {
    public ValidationException(String message) { super(message); }
}
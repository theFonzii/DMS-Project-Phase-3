package com.example.dms_project_phase_3_real;

import java.util.Scanner;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * Main.java
 * This application will spool up all classes to provide a command line interface
 * to the user, with which they will be able to interact with and manage a database
 * of Runescape items.
 */

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        ItemRepository repo = new ItemRepository();
        BackpackService backpack = new BackpackService(repo, 30.0);
        TextFileAdapter fileAdapter = new TextFileAdapter();

        CliController cli = new CliController(in, repo, backpack, fileAdapter);
        int exitCode = cli.start();


        System.exit(exitCode);
    }
}

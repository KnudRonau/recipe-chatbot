package com.dt199g.project;

import io.reactivex.rxjava3.core.Observable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * ...
 *
 * @author ...
 */
public class Project {

    /**
     * Main point of program entry.
     * @param args application arguments
     */
    static public void main(String... args) {

        new ProjectRunner().runProject();

        /*
            Dummy implementation. Replace with your own solution.

        try (InputStream inputStream = Project.class.getResourceAsStream("/tmp.txt")) {
            assert inputStream != null;
            System.out.println(new String(inputStream.readAllBytes()));
        } catch (IOException | AssertionError e) {
            e.printStackTrace();
        }


         */
    }
}

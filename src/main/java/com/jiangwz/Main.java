package com.jiangwz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 修改nginx.conf文件，查找upstream,
 */
public class Main {

    public static void main(String[] args) {
        // write your code here
        if (null == args) {
            System.out.println("args is required.");
            System.exit(1);
        }
        Param param = new Param();
        for (String str : args) {
            if (str.startsWith("-f=")) {
                param.setFile(str.substring(3));
            }
            if (str.startsWith("-ups=")) {
                param.setUpstream(str.substring(5));
            }
            if (str.startsWith("-on=")) {
                param.setOn(str.substring(4));
            }
            if (str.startsWith("-off=")) {
                param.setOff(str.substring(5));
            }
        }

        if (null == param.getFile()) {
            System.out.println("-f is required.");
            System.exit(1);
        }
        if (null == param.getUpstream()) {
            System.out.println("-ups is required.");
            System.exit(1);
        }
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(param.getFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (lines.isEmpty()) {
            System.out.println("file is empty");
            System.exit(1);
        }
        // 进入upstream
        List<String> lines2 = new ArrayList<>();
        boolean start = false;
        int blankNum = 0;
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                lines2.add(line);
                continue;
            }
            if (line.trim().startsWith("upstream ") && line.contains(param.getUpstream())) {
                lines2.add(line);
                start = true;
                blankNum = line.indexOf("upstream");
                continue;
            }
            if (start && param.getOn() != null && line.trim().contains("server ") && line.contains(param.getOn())) {
                lines2.add(space(blankNum + 4) + line.substring(line.indexOf("server")));
                continue;
            }
            if (start && param.getOff() != null && line.trim().contains("server ") && line.contains(param.getOff())) {
                if (line.trim().startsWith("#")) {
                    lines2.add(line);
                    continue;
                }
                lines2.add("#" + line);
                continue;
            }
            if (start && line.trim().startsWith("}")) {
                lines2.add(line);
                start = false;
                continue;
            }
            lines2.add(line);
        }

        for (String str : lines2) {
            System.out.println(str);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(param.getFile()))) {
            for (String str : lines2) {
                writer.write(str);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("success");
    }

    private static String space(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}

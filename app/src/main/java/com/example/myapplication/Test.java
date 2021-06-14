package com.example.myapplication;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Test {
    private static List<List<List<Boolean>>> result = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static  List<List<String>> solveNQueens(int n) {

        List<List<Boolean>> track = new ArrayList<List<Boolean>>();
        for (int b = 0; b < n; b++) {
            List<Boolean> rowTrack = new ArrayList<>();
            for (int c = 0; c < n; c++) {
                rowTrack.add(false);
            }
            track.add(rowTrack);
        }
        backtrack(n, track, 0);

        List<List<String>> finalResult = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            List<List<Boolean>> aResult = result.get(i);
            List<String> aFinalResult = new ArrayList<>();
            for (int j = 0; j < aResult.size(); j++) {
                List<Boolean> rowResult = aResult.get(j);
                StringBuilder sb = new StringBuilder();
                for (int z = 0; z < rowResult.size(); z++) {
                    sb.append(rowResult.get(z) ? "Q" : ".");
                }
                aFinalResult.add(sb.toString());
            }
            finalResult.add(aFinalResult);
        }
        return finalResult;
    }

    private static void backtrack(int totalNum, List<List<Boolean>> track, int row) {
        if (totalNum == row) {
            for (List<Boolean> booleans : track) {
                System.out.println(booleans.toString());
            }
            List<List<Boolean>> finalResult = new ArrayList<>();
            finalResult.addAll(track);
            result.add(finalResult);
            return;
        }
        for (int col = 0; col < totalNum; col++) {
            if (!isValid(totalNum, track, row, col)) {
                continue;
            }
            track.get(row).set(col, true);
            backtrack(totalNum, track, row + 1);
            track.get(row).set(col, false);
        }
    }

    private static boolean isValid(int totalNum, List<List<Boolean>> track, int row, int col) {
        for (int i = 0; i < row; i++) {
            if (track.get(i).get(col)) {
                return false;
            }
        }
        for (int i = row - 1, j = col + 1; i >= 0 && j < totalNum; i--, j++) {
            if (track.get(i).get(j)) {
                return false;
            }
        }
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (track.get(i).get(j)) {
                return false;
            }
        }

        return true;
    }
}

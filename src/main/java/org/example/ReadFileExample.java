package org.example;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class ReadFileExample {
    public static void main(String[] args) {
        String fileIn = "input.txt";
        String fileOut = "output.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileIn))) {
            String line;
            List<String> values = new ArrayList<>();
            List<List<Integer>> outputBid = new ArrayList<>();
            List<List<Integer>> outputAsk = new ArrayList<>();
            List<List<Integer>> query = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");

                switch (row[0]) {
                    case "u":
                        switch (row[3]) {
                            case "bid" -> {
                                Optional<List<Integer>> exists = outputBid.stream()
                                        .filter(p -> p.get(0).equals(Integer.parseInt(row[1])))
                                        .findFirst();
                                if (exists.isPresent()) {
                                    int index = outputBid.indexOf(exists.get());
                                    outputBid.set(index, Arrays.asList(Integer.parseInt(row[1]), Integer.parseInt(row[2])));
                                } else {
                                    outputBid.add(Arrays.asList(Integer.valueOf(row[1]), Integer.valueOf(row[2])));
                                }
                            }

                            case "ask" -> {
                                Optional<List<Integer>> exists = outputAsk.stream()
                                        .filter(p -> p.get(0).equals(Integer.parseInt(row[1])))
                                        .findFirst();
                                if (exists.isPresent()) {
                                    int index = outputAsk.indexOf(exists.get());
                                    outputAsk.set(index, Arrays.asList(Integer.parseInt(row[1]), Integer.parseInt(row[2])));
                                } else {
                                    outputAsk.add(Arrays.asList(Integer.parseInt(row[1]), Integer.parseInt(row[2])));
                                }
                            }
                        }
                    case "q":
                        if (row.length == 2) {
                            switch (row[1]) {
                                case "best_bid" -> query
                                        .add(outputBid.stream()
                                                .filter(p -> p.get(1) != 0)
                                                .max(Comparator.comparingInt(p -> p.get(0)))
                                                .orElse(Collections.singletonList(0)));
                                case "best_ask" -> query
                                        .add(outputAsk.stream()
                                                .filter(p -> p.get(1) != 0)
                                                .min(Comparator.comparingInt(p -> p.get(0)))
                                                .orElse(Collections.singletonList(0)));
                            }
                        } else if (row.length == 3) {
                            query
                                    .add(Collections.singletonList(Stream.concat(outputBid.stream(), outputAsk.stream())
                                            .filter(p -> Objects.equals(p.get(0), Integer.valueOf(row[2])))
                                            .min(Comparator.comparingInt(p -> p.get(0)))
                                            .map(p -> p.get(1))
                                            .orElse(0)));
                        }
                    case "o":
                        switch (row[1]) {
                            case "buy" -> {
                                List<Integer> minValue = outputAsk.stream()
                                        .filter(p -> p.get(1) >= Integer.parseInt(row[2]))
                                        .min(Comparator.comparingInt(p -> p.get(0)))
                                        .get();
                                minValue = new ArrayList<>(minValue);
                                minValue.set(1, minValue.get(1) - Integer.parseInt(row[2]));
                                List<Integer> finalMinValue = minValue;
                                outputAsk.stream()
                                        .filter(p -> p.get(1) >= Integer.parseInt(row[2]))
                                        .filter(p -> p.get(0).equals(finalMinValue.get(0)))
                                        .findFirst()
                                        .ifPresent(p -> outputAsk.set(outputAsk.indexOf(p), finalMinValue));
                            }
                            case "sell" -> {
                                List<Integer> maxValue = outputBid.stream()
                                        .filter(p -> p.get(1) >= Integer.parseInt(row[2]))
                                        .max(Comparator.comparingInt(p -> p.get(0)))
                                        .get();
                                maxValue = new ArrayList<>(maxValue);
                                maxValue.set(1, maxValue.get(1) - Integer.parseInt(row[2]));
                                List<Integer> finalMaxValue = maxValue;
                                outputBid.stream()
                                        .filter(p -> p.get(1) >= Integer.parseInt(row[2]))
                                        .filter(p -> {
//                                            System.out.println(p);
//                                            System.out.println("p[1]"+p.get(1));
//                                            System.out.println("r[2]"+Integer.parseInt(row[2]));
                                            return p.get(0).equals(finalMaxValue.get(0));
                                        })
                                        .findFirst()
                                        .ifPresent(p -> outputBid.set(outputBid.indexOf(p), finalMaxValue));
                            }
                        }
                }
                values.add(Arrays.toString(line.split(",")));
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileOut))) {
                for (List<Integer> lines : query) {
                    StringJoiner joiner = new StringJoiner(",");
                    for (int i : lines) {
                        joiner.add(String.valueOf(i));
                    }
                    String result = joiner.toString();
                    bw.write(result);
                    bw.newLine();
                }
            }
//            System.out.println(values);
//            System.out.println(query);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package ir.ac.guilan.ce;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        var maxwellGraph = new MaxwellGraph();
        maxwellGraph.create();
        maxwellGraph.getTheMaxwellNumbers().forEach(System.out::println);
    }

    private static class MaxwellGraph {
        boolean[][] adjacencyMatrix;
        int[] targetAuthors;

        void create() {
            try (var scanner = new Scanner(System.in)) {
                var numOfAuthors = scanner.nextInt();
                adjacencyMatrix = new boolean[numOfAuthors][numOfAuthors];
                var numOfArticles = scanner.nextInt();
                var numOfTargetAuthors = scanner.nextInt();

                IntStream.range(0, numOfArticles)
                        .map(i -> scanner.nextInt())
                        .mapToObj(numOfAuthorsOfArticle -> IntStream.range(0, numOfAuthorsOfArticle)
                                .map(i -> scanner.nextInt())
                                .toArray())
                        .forEach(this::connectAuthors);

                targetAuthors = IntStream.range(0, numOfTargetAuthors)
                        .map(i -> scanner.nextInt())
                        .toArray();
            }
        }

        void connectAuthors(int[] authorsOfArticle) {
            Arrays.stream(authorsOfArticle)
                    .forEach(author -> Arrays.stream(authorsOfArticle)
                            .filter(anotherAuthor -> author != anotherAuthor)
                            .forEach(anotherAuthor -> adjacencyMatrix[author][anotherAuthor] = true));
        }

        IntStream getTheMaxwellNumbers() {
            // TODO: 09/12/2021 Consider breaking the loop after finding the path to all the targets
            var sptSet = new HashSet<Integer>();
            var maxwellNumbers = new int[adjacencyMatrix.length];
            Arrays.fill(maxwellNumbers, Integer.MAX_VALUE);
            maxwellNumbers[0] = 0;

            while (sptSet.size() < adjacencyMatrix.length) {
                var nextAuthor = getNearestAuthor(maxwellNumbers, sptSet);
                sptSet.add(nextAuthor);
                updateAdjacentAuthors(nextAuthor, maxwellNumbers);
            }

            return Arrays.stream(targetAuthors)
                    .map(author -> maxwellNumbers[author] == Integer.MAX_VALUE ? -1 : maxwellNumbers[author]);
        }

        int getNearestAuthor(int[] maxwellNumbers, HashSet<Integer> sptSet) {
            return IntStream.range(0, maxwellNumbers.length)
                    .filter(author -> !sptSet.contains(author))
                    .boxed()
                    .min(Comparator.comparingInt(author -> maxwellNumbers[author]))
                    .orElseThrow();
        }

        void updateAdjacentAuthors(int author, int[] maxwellNumbers) {
            boolean[] adjacentVertices = adjacencyMatrix[author];
            IntStream.range(0, adjacentVertices.length)
                    .filter(i -> adjacentVertices[i] && (maxwellNumbers[author] < maxwellNumbers[i] - 1))
                    .forEach(i -> maxwellNumbers[i] = maxwellNumbers[author] + 1);
        }
    }
}
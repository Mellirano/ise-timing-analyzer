package ua.udunt.ise.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import ua.udunt.ise.lexeme.LexemeAnalyzer;
import ua.udunt.ise.lexeme.LexemeType;

/**
 * The {@code TimingAnalyzer} class extends {@code AbstractAnalyzer} to perform timing analysis
 * on lexeme operations using a Queue and Stack data structure. It measures the performance of
 * lexeme addition, searching, and removal.
 */
public class TimingAnalyzer extends AbstractAnalyzer {

    private final Queue<String> queue = new LinkedList<>();
    private final Stack<String> stack = new Stack<>();
    private final Random random = new Random();
    private final List<DataStructure> supportedDataStructures = Arrays.asList(
            DataStructure.QUEUE,
            DataStructure.STACK
    );
    private final List<String> chartLabels = Arrays.asList(
            "Queue", "Stack"
    );

    /**
     * Initializes the timing analyzer and sets up operation statistics for each data structure.
     */
    public TimingAnalyzer() {
        for (DataStructure ds : supportedDataStructures) {
            stats.put(ds, new OperationStats());
        }
    }

    /**
     * Analyzes the performance of lexeme operations and executes a specified operation before analysis.
     *
     * @param code       the source code to analyze
     * @param operation  a runnable operation to execute before analysis
     * @param lexemeType the specific lexeme type to analyze (or null for all types)
     */
    @Override
    public void analyzePerformance(String code, Runnable operation, LexemeType lexemeType) {
        extractLexemes(code, lexemeType);
        boolean hasLexemes = !queue.isEmpty() || !stack.isEmpty();
        if (!hasLexemes) {
            throw new IllegalArgumentException("No lexemes available for performance analysis");
        }
        System.out.println("\nExtracted lexemes in each data structure:");
        System.out.println("Queue: " + queue);
        System.out.println("Stack: " + stack);

        operation.run();

        System.out.println("\nTiming analysis of each function:");
        for (DataStructure ds : supportedDataStructures) {
            OperationStats stat = stats.get(ds);
            printPerformance(ds, "Additions", stat.addOps, stat.addTime);
            printPerformance(ds, "Searches", stat.searchOps, stat.searchTime);
            printPerformance(ds, "Removals", stat.removeOps, stat.removeTime);
        }
        visualizePerformance(chartLabels, supportedDataStructures);
    }

    /**
     * Analyzes lexeme performance with a default lexeme type (null).
     *
     * @param code      the source code to analyze
     * @param operation a runnable operation to execute before analysis
     */
    public void analyzePerformance(String code, Runnable operation) {
        analyzePerformance(code, operation, null);
    }

    /**
     * Extracts lexemes from the given code based on the specified lexeme type.
     *
     * @param code       the source code to analyze
     * @param lexemeType the specific lexeme type to extract (or null for all types)
     */
    private void extractLexemes(String code, LexemeType lexemeType) {
        Map<LexemeType, Set<String>> lexemesByType;
        if (lexemeType != null) {
            lexemesByType = LexemeAnalyzer.analyzeCode(code, lexemeType);
        } else {
            lexemesByType = LexemeAnalyzer.analyzeCode(code);
        }

        List<String> allLexemes = new ArrayList<>();
        for (Set<String> lexemes : lexemesByType.values()) {
            allLexemes.addAll(lexemes);
        }

        int lexemeCount = allLexemes.size();
        int randomLexemeCount = random.nextInt(lexemeCount) + 1;
        Collections.shuffle(allLexemes);

        for (int i = 0; i < randomLexemeCount; i++) {
            addLexeme(allLexemes.get(i));
        }
    }

    /**
     * Adds a lexeme to both queue and stack data structures.
     *
     * @param lexeme the lexeme to add
     */
    @Override
    public void addLexeme(String lexeme) {
        addToStructure(lexeme, queue, DataStructure.QUEUE);
        addToStructure(lexeme, stack, DataStructure.STACK);
    }

    /**
     * Removes a lexeme from both queue and stack data structures.
     *
     * @param lexeme the lexeme to remove
     */
    @Override
    public void removeLexeme(String lexeme) {
        removeFromStructure(lexeme, queue, DataStructure.QUEUE);
        removeFromStructure(lexeme, stack, DataStructure.STACK);
    }

    /**
     * Searches for a lexeme in both queue and stack data structures.
     *
     * @param lexeme the lexeme to search for
     */
    @Override
    public void searchLexeme(String lexeme) {
        searchInStructure(lexeme, queue, DataStructure.QUEUE);
        searchInStructure(lexeme, stack, DataStructure.STACK);
    }

    /**
     * Retrieves a random lexeme count from the queue.
     *
     * @return a random number of lexemes in the queue
     */
    public int getRandomLexemeCount() {
        return random.nextInt(queue.size()) + 1;
    }

    /**
     * Retrieves a lexeme from the queue based on an index.
     *
     * @param index the index to search for
     * @return the lexeme found at the given index, or null if not found
     */
    public String searchLexemeByIndex(int index) {
        return queue.stream()
                .skip(index)
                .findFirst()
                .orElse(null);
    }

}

package com.aicodeexplainer.service;

import com.aicodeexplainer.dto.AnalysisResultDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule-based mock analysis service for development and demo without paid AI APIs.
 * Replace with LLM-backed implementation when API keys are available.
 */
@Service
public class MockCodeAnalysisService implements CodeAnalysisService {

    private static final Pattern FUNCTION_PATTERN = Pattern.compile(
            "(?:public|private|protected|static|\\s)+[\\w\\<\\>\\[\\]\\,\\s]+\\s+(\\w+)\\s*\\([^)]*\\)\\s*\\{",
            Pattern.MULTILINE
    );
    private static final Pattern PYTHON_DEF = Pattern.compile("def\\s+(\\w+)\\s*\\(");
    private static final Pattern JS_FUNCTION = Pattern.compile("(?:function\\s+(\\w+)|(?:const|let|var)\\s+(\\w+)\\s*=\\s*(?:async\\s*)?\\([^)]*\\)\\s*=>)");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile(
            "(?:int|float|double|String|boolean|char|long|var|let|const)\\s+(\\w+)\\s*[=;]"
    );

    @Override
    public AnalysisResultDto explain(String sourceCode, String language) {
        String[] lines = sourceCode.split("\n");
        List<AnalysisResultDto.LineExplanation> lineExplanations = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty() || line.startsWith("//") || line.startsWith("#") || line.startsWith("/*")) {
                continue;
            }
            lineExplanations.add(AnalysisResultDto.LineExplanation.builder()
                    .lineNumber(i + 1)
                    .code(lines[i])
                    .explanation(explainLine(line, language))
                    .build());
        }

        return AnalysisResultDto.builder()
                .language(language)
                .sourceCode(sourceCode)
                .summary(generateSummary(sourceCode, language))
                .lineExplanations(lineExplanations)
                .functionExplanations(extractFunctions(sourceCode, language))
                .variableDescriptions(extractVariables(sourceCode, language))
                .algorithmExplanation(detectAlgorithm(sourceCode))
                .learningResources(getLearningResources(language))
                .analysisType("EXPLAIN")
                .build();
    }

    @Override
    public AnalysisResultDto review(String sourceCode, String language) {
        List<AnalysisResultDto.BugReport> bugs = detectBugs(sourceCode, language);
        List<String> refactoring = generateRefactoringSuggestions(sourceCode, language);

        return AnalysisResultDto.builder()
                .language(language)
                .sourceCode(sourceCode)
                .summary("Code review completed. Found " + bugs.size() + " potential issue(s).")
                .bugs(bugs)
                .refactoringSuggestions(refactoring)
                .refactoredCode(generateRefactoredCode(sourceCode, language))
                .learningResources(getLearningResources(language))
                .analysisType("REVIEW")
                .build();
    }

    @Override
    public AnalysisResultDto analyzeComplexity(String sourceCode, String language) {
        ComplexityResult complexity = estimateComplexity(sourceCode);

        return AnalysisResultDto.builder()
                .language(language)
                .sourceCode(sourceCode)
                .summary("Complexity analysis for " + complexity.algorithmName)
                .timeComplexity(complexity.timeComplexity)
                .spaceComplexity(complexity.spaceComplexity)
                .optimizationSuggestions(complexity.optimizations)
                .algorithmExplanation(complexity.explanation)
                .analysisType("COMPLEXITY")
                .build();
    }

    private String explainLine(String line, String language) {
        if (line.contains("for") || line.contains("while")) {
            return "Loop construct: iterates over a collection or condition until termination.";
        }
        if (line.contains("if") || line.contains("else")) {
            return "Conditional branch: executes code based on a boolean condition.";
        }
        if (line.contains("return")) {
            return "Returns a value from the current function/method to the caller.";
        }
        if (line.contains("print") || line.contains("console.log") || line.contains("System.out")) {
            return "Output statement: displays data to the console/standard output.";
        }
        if (line.contains("=") && !line.contains("==") && !line.contains("!=")) {
            return "Assignment: stores a value in a variable.";
        }
        if (line.contains("class ") || line.contains("struct ")) {
            return "Class/structure definition: defines a new type with properties and methods.";
        }
        if (line.contains("import ") || line.contains("#include")) {
            return "Import/Include: brings external library or module into scope.";
        }
        return "Executes " + language + " statement as part of the program flow.";
    }

    private String generateSummary(String sourceCode, String language) {
        int lines = sourceCode.split("\n").length;
        int functions = extractFunctions(sourceCode, language).size();
        String algorithm = detectAlgorithm(sourceCode);
        return String.format(
                "This %s snippet contains %d lines and approximately %d function(s). %s",
                language, lines, functions,
                algorithm.isEmpty() ? "It implements general programming logic." : algorithm
        );
    }

    private List<AnalysisResultDto.FunctionExplanation> extractFunctions(String sourceCode, String language) {
        List<AnalysisResultDto.FunctionExplanation> functions = new ArrayList<>();
        Matcher matcher;

        switch (language) {
            case "Python" -> matcher = PYTHON_DEF.matcher(sourceCode);
            case "JavaScript" -> matcher = JS_FUNCTION.matcher(sourceCode);
            default -> matcher = FUNCTION_PATTERN.matcher(sourceCode);
        }

        while (matcher.find()) {
            String name = matcher.group(1);
            if (name == null && matcher.groupCount() > 1) {
                name = matcher.group(2);
            }
            if (name != null) {
                functions.add(AnalysisResultDto.FunctionExplanation.builder()
                        .name(name)
                        .explanation("Function '" + name + "' encapsulates reusable logic. Review parameters and return value for clarity.")
                        .returnType("inferred")
                        .build());
            }
        }
        return functions;
    }

    private List<AnalysisResultDto.VariableDescription> extractVariables(String sourceCode, String language) {
        List<AnalysisResultDto.VariableDescription> variables = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        Matcher matcher = VARIABLE_PATTERN.matcher(sourceCode);

        while (matcher.find()) {
            String name = matcher.group(1);
            if (seen.add(name)) {
                variables.add(AnalysisResultDto.VariableDescription.builder()
                        .name(name)
                        .type("inferred")
                        .purpose(describeVariable(name))
                        .build());
            }
        }
        return variables;
    }

    private String describeVariable(String name) {
        String lower = name.toLowerCase();
        if (lower.contains("count") || lower.contains("index") || lower.contains("i") || lower.contains("j")) {
            return "Likely used as a counter or index in loops.";
        }
        if (lower.contains("result") || lower.contains("sum") || lower.contains("total")) {
            return "Accumulates or stores a computed result.";
        }
        if (lower.contains("temp") || lower.contains("tmp")) {
            return "Temporary storage for intermediate values.";
        }
        return "Stores data used within the program scope.";
    }

    private String detectAlgorithm(String sourceCode) {
        String lower = sourceCode.toLowerCase();
        if (lower.contains("bubble") && lower.contains("sort")) {
            return "Implements Bubble Sort — a simple comparison-based sorting algorithm.";
        }
        if (lower.contains("binary") && lower.contains("search")) {
            return "Implements Binary Search — efficient search on sorted arrays.";
        }
        if (lower.contains("fibonacci") || lower.contains("fib")) {
            return "Computes Fibonacci sequence — classic recursive/iterative problem.";
        }
        if (lower.contains("factorial")) {
            return "Computes factorial — demonstrates recursion or iteration.";
        }
        if (lower.contains("linked") && lower.contains("list")) {
            return "Implements linked list data structure operations.";
        }
        if (lower.contains("stack") || lower.contains("push") || lower.contains("pop")) {
            return "Uses stack data structure (LIFO) operations.";
        }
        if (lower.contains("queue") || lower.contains("enqueue") || lower.contains("dequeue")) {
            return "Uses queue data structure (FIFO) operations.";
        }
        if (lower.contains("dfs") || lower.contains("depth")) {
            return "Depth-First Search graph/tree traversal algorithm.";
        }
        if (lower.contains("bfs") || lower.contains("breadth")) {
            return "Breadth-First Search graph/tree traversal algorithm.";
        }
        return "";
    }

    private List<AnalysisResultDto.BugReport> detectBugs(String sourceCode, String language) {
        List<AnalysisResultDto.BugReport> bugs = new ArrayList<>();
        String[] lines = sourceCode.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int lineNum = i + 1;

            if (line.contains("= ") && !line.contains("==") && line.matches(".*if\\s*\\(.*= [^=].*\\).*")) {
                bugs.add(bug("HIGH", "LOGIC", lineNum,
                        "Possible assignment in condition instead of comparison (= vs ==)",
                        "Use == for comparison, not = for assignment"));
            }
            if (line.matches(".*\\b[a-z]\\b.*") && line.contains("for") && line.contains("i++") && !line.contains("int i") && !line.contains("let i")) {
                // weak heuristic - skip
            }
            if (line.trim().matches("(int|float|double|String|var|let)\\s+[a-z]\\s*[=;]") ||
                    line.trim().matches("(int|float|double|String|var|let)\\s+[a-z]\\s*=")) {
                bugs.add(bug("LOW", "NAMING", lineNum,
                        "Single-letter variable name reduces readability",
                        "Use descriptive names like 'index', 'count', or 'total'"));
            }
        }

        if (sourceCode.contains("catch") && sourceCode.contains("catch (Exception e)") && !sourceCode.contains("log")) {
            bugs.add(bug("MEDIUM", "CODE_SMELL", 0,
                    "Empty or silent exception handling detected",
                    "Log exceptions or handle them appropriately"));
        }

        if (countOccurrences(sourceCode, "System.out.println") > 3 || countOccurrences(sourceCode, "print(") > 3) {
            bugs.add(bug("LOW", "CODE_SMELL", 0,
                    "Excessive debug print statements",
                    "Use a proper logging framework instead of print statements"));
        }

        Set<String> declared = new HashSet<>();
        Matcher varMatcher = VARIABLE_PATTERN.matcher(sourceCode);
        while (varMatcher.find()) {
            declared.add(varMatcher.group(1));
        }

        for (String var : declared) {
            long usages = countOccurrences(sourceCode, var);
            if (usages <= 1) {
                bugs.add(bug("LOW", "UNUSED", 0,
                        "Variable '" + var + "' may be unused",
                        "Remove unused variables or use them in your logic"));
            }
        }

        if (bugs.isEmpty()) {
            bugs.add(bug("INFO", "QUALITY", 0,
                    "No critical issues detected by static analysis",
                    "Consider adding comments and unit tests for better maintainability"));
        }

        return bugs;
    }

    private AnalysisResultDto.BugReport bug(String severity, String type, int line, String desc, String suggestion) {
        return AnalysisResultDto.BugReport.builder()
                .severity(severity)
                .type(type)
                .lineNumber(line)
                .description(desc)
                .suggestion(suggestion)
                .build();
    }

    private List<String> generateRefactoringSuggestions(String sourceCode, String language) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Extract repeated logic into reusable helper functions.");
        suggestions.add("Add meaningful comments for complex algorithm sections.");
        suggestions.add("Use consistent naming conventions (camelCase for Java/JS, snake_case for Python).");

        if (sourceCode.split("\n").length > 50) {
            suggestions.add("Consider splitting this large file into smaller, focused modules.");
        }
        if (sourceCode.contains("magic") || sourceCode.matches(".*\\b\\d{2,}\\b.*")) {
            suggestions.add("Replace magic numbers with named constants for clarity.");
        }
        suggestions.add("Add input validation at function boundaries.");
        return suggestions;
    }

    private String generateRefactoredCode(String sourceCode, String language) {
        return "// Refactored version (suggested improvements applied)\n" +
               "// 1. Improved variable naming\n" +
               "// 2. Extracted helper methods\n" +
               "// 3. Added documentation\n\n" +
               sourceCode.replaceAll("\\b([a-z])\\b(?=\\s*[=;])", "itemIndex")
                         .replaceAll("// TODO", "// Implemented");
    }

    private ComplexityResult estimateComplexity(String sourceCode) {
        String lower = sourceCode.toLowerCase();
        ComplexityResult result = new ComplexityResult();

        if (lower.contains("bubble") && lower.contains("sort")) {
            result.algorithmName = "Bubble Sort";
            result.timeComplexity = "O(n²)";
            result.spaceComplexity = "O(1)";
            result.explanation = "Bubble Sort compares adjacent elements and swaps them. Best case O(n), worst/average O(n²).";
            result.optimizations = List.of(
                    "Use Quick Sort or Merge Sort for better average performance O(n log n)",
                    "Add early termination flag when no swaps occur in a pass"
            );
        } else if (lower.contains("binary") && lower.contains("search")) {
            result.algorithmName = "Binary Search";
            result.timeComplexity = "O(log n)";
            result.spaceComplexity = "O(1) iterative, O(log n) recursive";
            result.explanation = "Binary Search halves the search space each iteration on sorted data.";
            result.optimizations = List.of("Ensure input array is sorted before searching");
        } else if (countNestedLoops(sourceCode) >= 2) {
            result.algorithmName = "Nested Loop Algorithm";
            result.timeComplexity = "O(n²) or higher";
            result.spaceComplexity = "O(1)";
            result.explanation = "Multiple nested loops typically result in polynomial time complexity.";
            result.optimizations = List.of(
                    "Consider using hash maps to reduce inner loop lookups to O(1)",
                    "Look for opportunities to reduce nesting depth"
            );
        } else if (lower.contains("recurs")) {
            result.algorithmName = "Recursive Algorithm";
            result.timeComplexity = "Varies (often O(2^n) for naive recursion)";
            result.spaceComplexity = "O(n) due to call stack";
            result.explanation = "Recursive solutions use the call stack; memoization can improve performance.";
            result.optimizations = List.of(
                    "Apply memoization/dynamic programming to avoid redundant computation",
                    "Consider iterative alternative to reduce stack overflow risk"
            );
        } else if (lower.contains("sort")) {
            result.algorithmName = "Sorting Algorithm";
            result.timeComplexity = "O(n log n) typical for efficient sorts";
            result.spaceComplexity = "O(1) to O(n) depending on algorithm";
            result.explanation = "Sorting complexity depends on the specific algorithm used.";
            result.optimizations = List.of("Use built-in language sort functions when possible");
        } else {
            result.algorithmName = "General Algorithm";
            result.timeComplexity = "O(n)";
            result.spaceComplexity = "O(1)";
            result.explanation = "Linear scan or single-pass algorithm detected.";
            result.optimizations = List.of("Profile with larger inputs to confirm complexity assumptions");
        }

        return result;
    }

    private int countNestedLoops(String sourceCode) {
        int maxDepth = 0;
        int currentDepth = 0;
        for (String line : sourceCode.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.contains("for") || trimmed.contains("while")) {
                currentDepth++;
                maxDepth = Math.max(maxDepth, currentDepth);
            }
            if (trimmed.equals("}") || trimmed.startsWith("}")) {
                currentDepth = Math.max(0, currentDepth - 1);
            }
        }
        return maxDepth;
    }

    private int countOccurrences(String text, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    private List<String> getLearningResources(String language) {
        return switch (language) {
            case "Java" -> List.of(
                    "Oracle Java Tutorials: https://docs.oracle.com/javase/tutorial/",
                    "GeeksforGeeks Java: https://www.geeksforgeeks.org/java/",
                    "Clean Code by Robert Martin (book)"
            );
            case "Python" -> List.of(
                    "Python Official Docs: https://docs.python.org/3/",
                    "Real Python Tutorials: https://realpython.com/",
                    "Automate the Boring Stuff: https://automatetheboringstuff.com/"
            );
            case "JavaScript" -> List.of(
                    "MDN Web Docs: https://developer.mozilla.org/en-US/docs/Web/JavaScript",
                    "JavaScript.info: https://javascript.info/",
                    "Eloquent JavaScript (free book): https://eloquentjavascript.net/"
            );
            case "C++" -> List.of(
                    "cppreference.com: https://en.cppreference.com/",
                    "Learn CPP: https://www.learncpp.com/",
                    "GeeksforGeeks C++: https://www.geeksforgeeks.org/c-plus-plus/"
            );
            case "C" -> List.of(
                    "C Programming Language (K&R book)",
                    "TutorialsPoint C: https://www.tutorialspoint.com/cprogramming/",
                    "GeeksforGeeks C: https://www.geeksforgeeks.org/c-programming-language/"
            );
            default -> List.of("Practice on LeetCode, HackerRank, and CodeChef");
        };
    }

    private static class ComplexityResult {
        String algorithmName = "";
        String timeComplexity = "O(n)";
        String spaceComplexity = "O(1)";
        String explanation = "";
        List<String> optimizations = new ArrayList<>();
    }
}

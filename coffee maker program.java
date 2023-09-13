import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CoffeeMakerGUI {
    private JFrame frame;
    private JPanel panel;
    private JLabel titleLabel;
    private JLabel logoLabel;
    private JButton espressoButton;
    private JButton latteButton;
    private JButton cappuccinoButton;
    private JButton offButton;
    private JButton reportButton;
    private JTextArea outputTextArea;
    private Map<String, Map<String, Integer>> MENU;
    private Map<String, Integer> resources;
    private double profit;

    public CoffeeMakerGUI() {
        frame = new JFrame("Coffee Maker");
        frame.setSize(500, 500);
       // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        titleLabel = new JLabel("WELCOME TO COFFEE MAKER. WHAT WOULD YOU LIKE TO HAVE");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        String logo = "YOUR LOGO HERE";
        logoLabel = new JLabel(logo);
        panel.add(logoLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 2));

        espressoButton = new JButton("Espresso");
        latteButton = new JButton("Latte");
        cappuccinoButton = new JButton("Cappuccino");
        offButton = new JButton("Off");
        reportButton = new JButton("Report");

        espressoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleCoffeeSelection("espresso");
            }
        });

        latteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleCoffeeSelection("latte");
            }
        });

        cappuccinoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleCoffeeSelection("cappuccino");
            }
        });

        offButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleOffButtonClick();
            }
        });

        reportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleReportButtonClick();
            }
        });

        buttonPanel.add(espressoButton);
        buttonPanel.add(latteButton);
        buttonPanel.add(cappuccinoButton);
        buttonPanel.add(offButton);
        buttonPanel.add(reportButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        panel.add(scrollPane, BorderLayout.EAST);

        frame.add(panel);
        frame.setVisible(true);

        initializeCoffeeMaker();
    }

    private void initializeCoffeeMaker() {
        MENU = new HashMap<>();
        Map<String, Integer> espressoIngredients = new HashMap<>();
        espressoIngredients.put("water", 50);
        espressoIngredients.put("coffee", 18);
        MENU.put("espresso", espressoIngredients);

        Map<String, Integer> latteIngredients = new HashMap<>();
        latteIngredients.put("water", 200);
        latteIngredients.put("milk", 150);
        latteIngredients.put("coffee", 24);
        MENU.put("latte", latteIngredients);

        Map<String, Integer> cappuccinoIngredients = new HashMap<>();
        cappuccinoIngredients.put("water", 250);
        cappuccinoIngredients.put("coffee", 24);
        cappuccinoIngredients.put("milk", 100);
        MENU.put("cappuccino", cappuccinoIngredients);

        resources = new HashMap<>();
        resources.put("water", 300);
        resources.put("milk", 200);
        resources.put("coffee", 100);

        profit = 0;
    }

    private void handleCoffeeSelection(String choice) {
        if (isResourcesSufficient(choice, MENU, resources)) {
            double payment = processCoins();
            if (isTransactionSuccessful(choice, MENU, resources, payment)) {
                makeCoffee(choice, MENU, resources);
            }
        }
    }

    private boolean isResourcesSufficient(String choice, Map<String, Map<String, Integer>> MENU, Map<String, Integer> resources) {
        Map<String, Integer> orderIngredients = MENU.get(choice);
        if (orderIngredients == null) {
            output("Invalid choice.");
            return false;
        }

        for (String item : orderIngredients.keySet()) {
            if (orderIngredients.get(item) > resources.getOrDefault(item, 0)) {
                output("Sorry, there is not enough " + item);
                return false;
            }
        }
        return true;
    }

    private double processCoins() {
        output("Please insert coins:");
        int quarters = getInput("How many Quarters? ");
        int dimes = getInput("How many Dimes? ");
        int nickels = getInput("How many Nickels? ");
        int pennies = getInput("How many Pennies? ");

        return quarters * 0.25 + dimes * 0.1 + nickels * 0.05 + pennies * 0.01;
    }

    private int getInput(String message) {
        String input = JOptionPane.showInputDialog(message);
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean isTransactionSuccessful(String choice, Map<String, Map<String, Integer>> MENU, Map<String, Integer> resources, double payment) {
        double cost = getCost(choice, MENU);
        if (payment >= cost) {
            double change = payment - cost;
            output("Here is $" + String.format("%.2f", change) + " in change.");
            profit += cost;

            for (String item : MENU.get(choice).keySet()) {
                resources.put(item, resources.get(item) - MENU.get(choice).get(item));
            }

            return true;
        } else {
            output("Sorry, not enough money. Money refunded.");
            return false;
        }
    }

    private double getCost(String choice, Map<String, Map<String, Integer>> MENU) {
        return MENU.get(choice).entrySet().stream()
                .mapToDouble(entry -> entry.getValue() * getItemCost(entry.getKey()))
                .sum();
    }

    private double getItemCost(String item) {
        switch (item) {
            case "water":
                return 0.1;
            case "milk":
                return 0.15;
            case "coffee":
                return 0.2;
            default:
                return 0.0;
        }
    }

    private void makeCoffee(String choice, Map<String, Map<String, Integer>> MENU, Map<String, Integer> resources) {
        output("Dispensing your " + choice + "...");
    }

    private void handleOffButtonClick() {
        output("Turning off the coffee maker.");
        System.exit(0);
    }

    private void handleReportButtonClick() {
        output("Water: " + resources.get("water") + " ml");
        output("Milk: " + resources.get("milk") + " ml");
        output("Coffee: " + resources.get("coffee") + " g");
        output("Profit: $" + profit);
    }

    private void output(String message) {
        outputTextArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CoffeeMakerGUI();
            }
        });
    }
}

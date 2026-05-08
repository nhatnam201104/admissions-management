package com.example.managementadmissionwf.ui.panel;

import com.example.managementadmissionwf.config.ApplicationContextHolder;
import com.example.managementadmissionwf.dto.statistic.MajorStatistic;
import com.example.managementadmissionwf.dto.statistic.MethodStatistic;
import com.example.managementadmissionwf.dto.statistic.ScoreDistribution;
import com.example.managementadmissionwf.dto.statistic.StatisticSummary;
import com.example.managementadmissionwf.ui.component.StatisticController;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * StatisticPanel - Dashboard thống kê xét tuyển
 * Sử dụng JFreeChart cho các biểu đồ
 */
@Slf4j
@Component
public class StatisticPanel extends JPanel {
    
    private StatisticController controller;
    
    // Summary Cards
    private JLabel lblTotalCandidates;
    private JLabel lblTotalAspirations;
    private JLabel lblAdmissionRate;
    private JLabel lblAvgScore;
    
    // Chart Panels
    private ChartPanel majorChartPanel;
    private ChartPanel methodChartPanel;
    private ChartPanel scoreChartPanel;
    
    // Chart Containers (để update sau khi load data)
    private JPanel majorChartContainer;
    private JPanel methodChartContainer;
    private JPanel scoreChartContainer;
    
    public StatisticPanel() {
        this.controller = ApplicationContextHolder.getBean(StatisticController.class);
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // ===== TOP: Toolbar =====
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // ===== CENTER: Content =====
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        
        // Summary Cards
        JPanel summaryPanel = createSummaryPanel();
        centerPanel.add(summaryPanel, BorderLayout.NORTH);
        
        // Main charts area
        JPanel chartsPanel = new JPanel(new BorderLayout(10, 10));
        chartsPanel.setBackground(Color.WHITE);
        
        // Major statistics chart (top, full width)
        majorChartContainer = createChartContainer("Thống kê theo ngành (Top 10)", majorChartPanel);
        chartsPanel.add(majorChartContainer, BorderLayout.NORTH);
        
        // Bottom row: Score distribution + Method statistics
        JPanel bottomCharts = new JPanel(new GridLayout(1, 2, 10, 10));
        bottomCharts.setBackground(Color.WHITE);
        
        scoreChartContainer = createChartContainer("Phân bố điểm", scoreChartPanel);
        methodChartContainer = createChartContainer("Theo phương thức xét tuyển", methodChartPanel);
        
        bottomCharts.add(scoreChartContainer);
        bottomCharts.add(methodChartContainer);
        chartsPanel.add(bottomCharts, BorderLayout.CENTER);
        
        centerPanel.add(chartsPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        
        // Title
        JLabel title = new JLabel("Thống kê xét tuyển");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(44, 62, 80));
        panel.add(title, BorderLayout.WEST);
        
        // Refresh button only
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(Color.WHITE);
        
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        
        buttons.add(btnRefresh);
        panel.add(buttons, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(Color.WHITE);
        
        // Card 1: Total Candidates
        JPanel card1 = createSummaryCard("Tổng thí sinh", lblTotalCandidates = new JLabel("0"),
            new Color(52, 152, 219));
        
        // Card 2: Total Aspirations
        JPanel card2 = createSummaryCard("Tổng nguyện vọng", lblTotalAspirations = new JLabel("0"),
            new Color(155, 89, 182));
        
        // Card 3: Admission Rate
        JPanel card3 = createSummaryCard("Tỷ lệ trúng tuyển", lblAdmissionRate = new JLabel("0%"), 
            new Color(46, 204, 113));
        
        // Card 4: Average Score
        JPanel card4 = createSummaryCard("Điểm TB", lblAvgScore = new JLabel("0.0"),
            new Color(230, 126, 34));
        
        panel.add(card1);
        panel.add(card2);
        panel.add(card3);
        panel.add(card4);
        
        return panel;
    }
    
    private JPanel createSummaryCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(127, 140, 141));
        
        // Value
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(valueLabel);
        
        card.add(centerPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createChartContainer(String title, ChartPanel chartPanel) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(44, 62, 80));
        container.add(titleLabel, BorderLayout.NORTH);
        
        if (chartPanel != null) {
            container.add(chartPanel, BorderLayout.CENTER);
        }
        
        return container;
    }
    
    public void loadData() {
        try {
            // Load summary
            StatisticSummary summary = controller.loadSummary();
            SwingUtilities.invokeLater(() -> {
                lblTotalCandidates.setText(String.format("%,d", summary.totalCandidates()));
                lblTotalAspirations.setText(String.format("%,d", summary.totalAspirations()));
                lblAdmissionRate.setText(String.format("%.1f%%", summary.admissionRate()));
                lblAvgScore.setText(String.format("%.1f", summary.avgScore()));
            });
            
            // Load major statistics
            java.util.List<MajorStatistic> majorStats = controller.loadMajorStatistics();
            SwingUtilities.invokeLater(() -> {
                majorChartPanel = createMajorBarChart(majorStats);
                updateChartPanel("Thống kê theo ngành (Top 10)", majorChartPanel, 0);
            });
            
            // Load method statistics
            java.util.List<MethodStatistic> methodStats = controller.loadMethodStatistics();
            SwingUtilities.invokeLater(() -> {
                methodChartPanel = createMethodPieChart(methodStats);
                updateChartPanel("Theo phương thức xét tuyển", methodChartPanel, 1);
            });
            
            // Load score distribution
            java.util.List<ScoreDistribution> scoreDist = controller.loadScoreDistribution();
            SwingUtilities.invokeLater(() -> {
                scoreChartPanel = createScoreBarChart(scoreDist);
                updateChartPanel("Phân bố điểm", scoreChartPanel, 2);
            });
            
        } catch (Exception e) {
            log.error("Error loading data", e);
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void updateChartPanel(String title, ChartPanel newChartPanel, int index) {
        JPanel container;
        switch (index) {
            case 0 -> container = majorChartContainer;
            case 1 -> container = methodChartContainer;
            case 2 -> container = scoreChartContainer;
            default -> { return; }
        }
        
        if (container != null && newChartPanel != null) {
            // Xóa chart cũ (nếu có)
            java.awt.Component[] components = container.getComponents();
            for (java.awt.Component comp : components) {
                if (comp instanceof ChartPanel) {
                    container.remove(comp);
                }
            }
            
            // Thêm chart mới
            container.add(newChartPanel, BorderLayout.CENTER);
            container.revalidate();
            container.repaint();
        }
    }
    
    private ChartPanel createMajorBarChart(java.util.List<MajorStatistic> stats) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (MajorStatistic stat : stats) {
            dataset.addValue(stat.totalAspirations(), "Nguyện vọng", stat.tennganh());
            dataset.addValue(stat.admitted(), "Trúng tuyển", stat.tennganh());
            dataset.addValue(stat.target(), "Chỉ tiêu", stat.tennganh());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Top ngành",
            "Ngành",
            "Số lượng",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        chart.setBackgroundPaint(Color.WHITE);
        
        return new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(600, 250);
            }
        };
    }
    
    private ChartPanel createMethodPieChart(java.util.List<MethodStatistic> stats) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        for (MethodStatistic stat : stats) {
            String label = stat.phuongThuc() != null ? stat.phuongThuc() : "Khác";
            dataset.setValue(label + " (" + stat.total() + ")", stat.total());
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Phương thức xét tuyển",
            dataset,
            true,
            true,
            false
        );
        
        chart.setBackgroundPaint(Color.WHITE);
        
        return new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(350, 250);
            }
        };
    }
    
    private ChartPanel createScoreBarChart(java.util.List<ScoreDistribution> stats) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (ScoreDistribution stat : stats) {
            dataset.addValue(stat.count(), "Số thí sinh", stat.range());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Phân bố điểm xét tuyển",
            "Khoảng điểm",
            "Số thí sinh",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        
        chart.setBackgroundPaint(Color.WHITE);
        
        return new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(350, 250);
            }
        };
    }
    
}
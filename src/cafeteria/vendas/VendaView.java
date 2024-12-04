package cafeteria.vendas;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import cafeteria.clientes.Cliente;
import cafeteria.clientes.IClienteService;
import cafeteria.produtos.IProdutoService;
import cafeteria.produtos.Produto;

/**
 * Classe responsável pela visualização e gerenciamento das vendas na cafeteria.
 */
public class VendaView extends JInternalFrame {

    private static final String TITULO = "Registro de Venda";
    private static final int POSICAO_X_INICIAL = 120;
    private static final int POSICAO_Y_INICIAL = 120;
    private static final int LARGURA = 750;
    private static final int ALTURA = 675;

    private static final int COLUNA_SELECAO = 0;
    private static final int COLUNA_NOME = 1;
    private static final int COLUNA_VALOR_UNITARIO = 2;
    private static final int COLUNA_QUANTIDADE = 3;
    private static final int COLUNA_VALOR_TOTAL = 4;

    private static final long serialVersionUID = 1L;

    private JTextField id;
    private JTextField nomeCliente;
    private JComboBox<Produto> produto;
    private JFormattedTextField quantidade;
    private JFormattedTextField desconto;
    private JFormattedTextField totalVenda;
    private JTextField medida;

    private JTable table;
    private DefaultTableModel model;

    private JButton btConfirmar;
    private JButton btCancelar;
    private JButton btBuscarCliente;
    private JButton btAdicionarItem;
    private JButton btRemoverItensSelecionados;

    private IVendaService vendaService;
    private IClienteService clienteService;
    private IProdutoService produtoService;

    private List<ItemVenda> itens;
    private Cliente clienteSelecionado;
    private double totalAtual = 0.0;

    /**
     * Construtor da classe VendaView.
     */
    public VendaView(IVendaService vendaService, IClienteService clienteService, IProdutoService produtoService) {
        this.vendaService = vendaService;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.itens = new ArrayList<>();

        configurarFrame();
        criarComponentes();
        configurarTabela();
        carregarProdutos();
        setupRegistrarNovaVenda();
    }

    // Configura as propriedades básicas do frame.
    private void configurarFrame() {
        setClosable(true);
        setIconifiable(true);
        setSize(LARGURA, ALTURA);
        setLocation(POSICAO_X_INICIAL, POSICAO_Y_INICIAL);
        setTitle(TITULO);
        getContentPane().setLayout(null);
    }

    // Coloca a lista de produtos no combo box.
    private void carregarProdutos() {
        try {
            List<Produto> produtos = produtoService.listarTodos();
            DefaultComboBoxModel<Produto> model = new DefaultComboBoxModel<>(produtos.toArray(new Produto[0]));
            produto.setModel(model);
        } catch (Exception e) {
            mostrarMensagemErro("Erro ao carregar produtos: " + e.getMessage());
        }
    }

    // Busca um cliente pelo ID informado.
    protected void onClickBuscarCliente() {
        try {
            String idText = id.getText().trim();
            if (idText.isEmpty()) {
                mostrarMensagemErro("Informe o ID do cliente");
                return;
            }

            Long clienteId = Long.parseLong(idText);
            clienteSelecionado = clienteService.buscarPorId(clienteId);

            if (clienteSelecionado != null) {
                nomeCliente.setText(clienteSelecionado.getNome());
                habilitarCamposVenda(true);
            } else {
                mostrarMensagemErro("Cliente não encontrado");
                limparCamposCliente();
                habilitarCamposVenda(false);
            }
        } catch (NumberFormatException e) {
            mostrarMensagemErro("ID do cliente inválido");
        } catch (Exception e) {
            mostrarMensagemErro("Erro ao buscar cliente: " + e.getMessage());
        }
    }


    // Cancela a venda atual.
    protected void onClickCancelar() {
        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente cancelar esta venda?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (opcao == JOptionPane.YES_OPTION) {
            limparCampos();
            setupRegistrarNovaVenda();
        }
    }


    // Registra a venda com os itens selecionados.
    protected void onClickRegistrarVenda() {
        try {
            if (clienteSelecionado == null) {
                mostrarMensagemErro("Selecione um cliente");
                return;
            }

            if (itens.isEmpty()) {
                mostrarMensagemErro("Adicione pelo menos um item");
                return;
            }

            Venda venda = new Venda();
            venda.setClienteId(clienteSelecionado.getId());
            venda.setDesconto(((Number) desconto.getValue()).doubleValue());
            venda.setItens(itens);

            vendaService.registrarVenda(venda);
            JOptionPane.showMessageDialog(this, "Venda registrada com sucesso!");
            limparCampos();
            setupRegistrarNovaVenda();

        } catch (Exception e) {
            mostrarMensagemErro("Erro ao registrar venda: " + e.getMessage());
        }
    }

    // Adiciona um item à venda.
    protected void onClickAdicionarItemVenda() {
        try {
            Produto produtoSelecionado = (Produto) produto.getSelectedItem();
            if (produtoSelecionado == null) {
                mostrarMensagemErro("Selecione um produto");
                return;
            }

            int qtd = ((Number) quantidade.getValue()).intValue();
            if (qtd <= 0) {
                mostrarMensagemErro("Quantidade deve ser maior que zero");
                return;
            }

            if (qtd > produtoSelecionado.getEstoque()) {
                mostrarMensagemErro("Quantidade não disponível no estoque");
                return;
            }

            ItemVenda item = new ItemVenda(
                    produtoSelecionado.getNome(),
                    produtoSelecionado.getMedida().ordinal(),
                    qtd,
                    produtoSelecionado.getPreco()
            );

            itens.add(item);
            model.addRow(new Object[]{
                    Boolean.FALSE,
                    item.getNome(),
                    item.getPreco(),
                    item.getQuantidade(),
                    item.getTotal()
            });

            atualizarTotal();
            limparCamposProduto();

        } catch (Exception e) {
            mostrarMensagemErro("Erro ao adicionar item: " + e.getMessage());
        }
    }

    // Remove os itens selecionados da tabela.
    protected void onClickRemoverItensSelecionados() {
        List<Integer> linhasSelecionadas = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((Boolean) model.getValueAt(i, COLUNA_SELECAO)) {
                linhasSelecionadas.add(i);
            }
        }

        if (linhasSelecionadas.isEmpty()) {
            mostrarMensagemErro("Selecione pelo menos um item para remover");
            return;
        }

        Collections.reverse(linhasSelecionadas);
        for (int linha : linhasSelecionadas) {
            model.removeRow(linha);
            itens.remove(linha);
        }

        atualizarTotal();
    }


    // Atualiza o total da venda considerando o desconto.
    private void atualizarTotal() {
        totalAtual = itens.stream()
                .mapToDouble(ItemVenda::getTotal)
                .sum();

        double descontoValor = ((Number) desconto.getValue()).doubleValue();
        totalVenda.setValue(totalAtual - descontoValor);
    }

    // Limpa todos os campos e reseta o estado da venda.
    private void limparCampos() {
        limparCamposCliente();
        limparCamposProduto();
        limparTabela();
        desconto.setValue(0.0);
        totalVenda.setValue(0.0);
        clienteSelecionado = null;
        itens.clear();
    }

    // Limpa os campos relacionados ao cliente.
    private void limparCamposCliente() {
        id.setText("");
        nomeCliente.setText("");
    }

    // Limpa os campos relacionados ao produto.
    private void limparCamposProduto() {
        produto.setSelectedIndex(-1);
        quantidade.setValue(0);
        medida.setText("");
    }

    // Limpa a tabela de itens.
    private void limparTabela() {
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }

    // Habilita ou desabilita os campos de venda.
    private void habilitarCamposVenda(boolean habilitar) {
        produto.setEnabled(habilitar);
        quantidade.setEnabled(habilitar);
        desconto.setEnabled(habilitar);
        btAdicionarItem.setEnabled(habilitar);
        btRemoverItensSelecionados.setEnabled(habilitar);
        btConfirmar.setEnabled(habilitar);
        btCancelar.setEnabled(habilitar);
    }

    // Configura o estado inicial para registrar uma nova venda.
    public void setupRegistrarNovaVenda() {
        limparCampos();
        id.setEnabled(true);
        btBuscarCliente.setEnabled(true);
        habilitarCamposVenda(false);
    }

    // Cria os componentes da interface gráfica.
    private void criarComponentes() {
        criarLabel("Cliente ID:", 31, 40);
        id = criarTextField(109, 38, 114, 21, SwingConstants.CENTER);

        criarLabel("Nome:", 31, 73);
        nomeCliente = criarTextField(109, 71, 600, 21, false);

        criarLabel("Produto:", 31, 106);
        produto = criarComboBox(109, 104, 600, 21);
        produto.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    Produto produtoSelecionado = (Produto) event.getItem();
                    if (produtoSelecionado != null) {
                        medida.setText(produtoSelecionado.getMedida().getDescricao());
                    }
                }
            }
        });

        criarLabel("Medida:", 31, 139);
        medida = criarTextField(109, 137, 114, 21, false);

        criarLabel("Quantidade:", 31, 172);
        quantidade = criarFormattedTextField(109, 170, 114, 21, NumberFormat.getIntegerInstance());

        criarLabel("Desconto R$:", 31, 534);
        desconto = criarFormattedTextField(109, 532, 114, 21, NumberFormat.getCurrencyInstance());
        desconto.addPropertyChangeListener("value", evt -> atualizarTotal());

        criarLabel("Total R$:", 31, 563);
        totalVenda = criarFormattedTextField(109, 561, 114, 21, NumberFormat.getCurrencyInstance());
        totalVenda.setEditable(false);

        criarBotoes();
    }


    // Cria um JLabel.
    private void criarLabel(String texto, int x, int y) {
        JLabel label = new JLabel(texto);
        label.setBounds(x, y, 72, 17);
        getContentPane().add(label);
    }

    // Cria um JTextField.
    private JTextField criarTextField(int x, int y, int largura, int altura, boolean editavel) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, largura, altura);
        textField.setEditable(editavel);
        getContentPane().add(textField);
        return textField;
    }

    // Cria um JTextField com alinhamento específico.
    private JTextField criarTextField(int x, int y, int largura, int altura, int alinhamento) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, largura, altura);
        textField.setHorizontalAlignment(alinhamento);
        getContentPane().add(textField);
        return textField;
    }

    // Cria um JComboBox.
    private JComboBox<Produto> criarComboBox(int x, int y, int largura, int altura) {
        JComboBox<Produto> comboBox = new JComboBox<>();
        comboBox.setBounds(x, y, largura, altura);
        getContentPane().add(comboBox);
        return comboBox;
    }

    // Cria um JFormattedTextField.
    private JFormattedTextField criarFormattedTextField(int x, int y, int largura, int altura, NumberFormat format) {
        JFormattedTextField formattedTextField = new JFormattedTextField(format);
        formattedTextField.setValue(0);
        formattedTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        formattedTextField.setBounds(x, y, largura, altura);
        getContentPane().add(formattedTextField);
        return formattedTextField;
    }

    // Cria os botões da interface gráfica.
    private void criarBotoes() {
        btConfirmar = criarBotao("Registrar Venda", 558, 585, e -> onClickRegistrarVenda());
        btCancelar = criarBotao("Cancelar", 441, 585, e -> onClickCancelar());
        btBuscarCliente = criarBotao("Buscar Cliente", 235, 35, e -> onClickBuscarCliente());
        btAdicionarItem = criarBotao("Adicionar Produto", 507, 203, e -> onClickAdicionarItemVenda());
        btRemoverItensSelecionados = criarBotao("Remover Itens Selecionados", 507, 516, e -> onClickRemoverItensSelecionados());
    }

    // Cria um JButton.
    private JButton criarBotao(String texto, int x, int y, java.awt.event.ActionListener acao) {
        JButton botao = new JButton(texto);
        botao.setBounds(x, y, 151, 27);
        botao.addActionListener(acao);
        getContentPane().add(botao);
        return botao;
    }

    // Configura a tabela de itens da venda.
    private void configurarTabela() {
        model = new DefaultTableModel(
                new Object[]{"", "Produto", "Preço R$", "Quantidade", "Total R$"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == COLUNA_SELECAO;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == COLUNA_SELECAO) return Boolean.class;
                if (columnIndex == COLUNA_VALOR_UNITARIO || columnIndex == COLUNA_VALOR_TOTAL) return Double.class;
                if (columnIndex == COLUNA_QUANTIDADE) return Integer.class;
                return String.class;
            }
        };

        table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(480, 250));
        table.setFillsViewportHeight(true);
        configurarColunasTabela();
        configurarCliqueTabela();

        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setBounds(31, 242, 678, 262);
        getContentPane().add(panel);
    }

    // Configura as colunas da tabela.
    private void configurarColunasTabela() {
        table.getColumnModel().getColumn(COLUNA_SELECAO).setMaxWidth(40);
        table.getColumnModel().getColumn(COLUNA_NOME).setPreferredWidth(200);
        table.getColumnModel().getColumn(COLUNA_VALOR_UNITARIO).setCellRenderer(new MoneyRenderer());
        table.getColumnModel().getColumn(COLUNA_VALOR_UNITARIO).setMaxWidth(100);
        table.getColumnModel().getColumn(COLUNA_QUANTIDADE).setMaxWidth(100);
        table.getColumnModel().getColumn(COLUNA_VALOR_TOTAL).setCellRenderer(new MoneyRenderer());
        table.getColumnModel().getColumn(COLUNA_VALOR_TOTAL).setMaxWidth(100);
    }


    // Configura o comportamento de clique na tabela.
    private void configurarCliqueTabela() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == COLUNA_SELECAO) {
                    boolean valor = (Boolean) table.getValueAt(row, col);
                    table.setValueAt(!valor, row, col);
                }
            }
        });
    }

    // Renderer para valores monetários na tabela.
    private class MoneyRenderer extends DefaultTableCellRenderer {
        private final NumberFormat formatter = NumberFormat.getCurrencyInstance();

        public MoneyRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            value = formatter.format(value);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    // Exibe uma mensagem de erro em um JOptionPane.
    private void mostrarMensagemErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}

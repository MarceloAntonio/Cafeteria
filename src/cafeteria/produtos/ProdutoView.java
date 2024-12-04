package cafeteria.produtos;

import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DocumentFilter;
import javax.swing.text.NumberFormatter;

/**
 * Classe responsável pela visualização e interação dos dados do produto.
 */
public class ProdutoView extends JInternalFrame {

    private static final String TITULO = "Cadastro de Produto";
    private static final int POSICAO_X_INICIAL = 60;
    private static final int POSICAO_Y_INICIAL = 60;
    private static final int LARGURA = 580;
    private static final int ALTURA = 300;
    private static final long serialVersionUID = 1L;

    private JTextField id; // Campo para o ID do produto
    private JTextField nome; // Campo para o nome do produto
    private JComboBox<UnidadeMedida> medida; // ComboBox para selecionar a unidade de medida
    private JFormattedTextField preco; // Campo formatado para o preço do produto
    private JTextField estoque; // Campo para a quantidade em estoque
    private JCheckBox cbTemEstoque; // Checkbox para indicar se o produto tem estoque

    private JButton btSalvar; // Botão para salvar o produto
    private JButton btVoltar; // Botão para voltar à tela anterior
    private JButton btNovoProduto; // Botão para incluir um novo produto
    private JButton btPesquisar; // Botão para pesquisar um produto

    private IProdutoService service; // Serviço para manipulação de produtos

    /**
     * Construtor da classe ProdutoView.
     */
    public ProdutoView(IProdutoService service) {
        this.service = service;
        configurarJanela();
        inicializarComponentes();
        configurarFiltrosCampos();
    }

    /**
     * Configura a janela do formulário.
     */
    private void configurarJanela() {
        setClosable(true);
        setIconifiable(true);
        setSize(LARGURA, ALTURA);
        setLocation(POSICAO_X_INICIAL, POSICAO_Y_INICIAL);
        setTitle(TITULO);
        getContentPane().setLayout(null);
    }

    /**
     * Inicializa a interface gráfica.
     */
    private void inicializarComponentes() {
        adicionarLabel("ID:", 31, 40);
        id = criarCampoTexto(109, 38, 114, 21, SwingConstants.CENTER);
        
        adicionarLabel("Nome:", 31, 73);
        nome = criarCampoTexto(109, 71, 430, 21, SwingConstants.LEFT);
        
        adicionarLabel("Medida:", 31, 106);
        medida = new JComboBox<>(UnidadeMedida.values());
        medida.setBounds(109, 104, 114, 21);
        getContentPane().add(medida);
        
        adicionarLabel("Preço:", 31, 136);
        preco = criarCampoPreco(109, 137, 114, 21);
        
        adicionarLabel("Estoque:", 31, 169);
        estoque = criarCampoTexto(109, 167, 114, 21, SwingConstants.RIGHT);
        estoque.setText("0");

        cbTemEstoque = new JCheckBox("Tem Estoque");
        cbTemEstoque.setBounds(31, 200, 120, 27);
        cbTemEstoque.addActionListener(e -> {
            estoque.setEnabled(cbTemEstoque.isSelected());
            if (!cbTemEstoque.isSelected()) {
                estoque.setText("0");
            }
        });
        cbTemEstoque.setEnabled(false);
        getContentPane().add(cbTemEstoque);

        btSalvar = criarBotao("Salvar", 434, 229, e -> onClickSalvar());
        btVoltar = criarBotao("Voltar", 322, 229, e -> onClickVoltar());
        btNovoProduto = criarBotao("Novo Produto", 400, 35, e -> onClickIncluirNovoProduto());
        btPesquisar = criarBotao("Pesquisar", 235, 35, e -> onClickPesquisar());
    }

    /**
     * Adiciona um rótulo (label) à interface gráfica.
     * @param texto Texto do rótulo.
     * @param x Posição X do rótulo.
     * @param y Posição Y do rótulo.
     * @return O JLabel criado.
     */
    private JLabel adicionarLabel(String texto, int x, int y) {
        JLabel label = new JLabel(texto);
        label.setBounds(x, y, 60, 17);
        getContentPane().add(label);
        return label;
    }

    /**
     * Cria um campo de texto na interface gráfica.
     * @param x Posição X do campo.
     * @param y Posição Y do campo.
     * @param largura Largura do campo.
     * @param altura Altura do campo.
     * @param alinhamento Alinhamento do texto no campo.
     * @return O JTextField criado.
     */
    private JTextField criarCampoTexto(int x, int y, int largura, int altura, int alinhamento) {
        JTextField campoTexto = new JTextField();
        campoTexto.setBounds(x, y, largura, altura);
        campoTexto.setHorizontalAlignment(alinhamento);
        getContentPane().add(campoTexto);
        return campoTexto;
    }

    /**
     * Cria um campo de preço formatado na interface gráfica.
     * @param x Posição X do campo.
     * @param y Posição Y do campo.
     * @param largura Largura do campo.
     * @param altura Altura do campo.
     * @return O JFormattedTextField criado.
     */
    private JFormattedTextField criarCampoPreco(int x, int y, int largura, int altura) {
        JFormattedTextField campoPreco = new JFormattedTextField();
        campoPreco.setHorizontalAlignment(SwingConstants.RIGHT);
        campoPreco.setBounds(x, y, largura, altura);
        getContentPane().add(campoPreco);
        configurarCampoPreco(campoPreco);
        return campoPreco;
    }

    /**
     * Cria um botão na interface gráfica.
     * @param texto Texto do botão.
     * @param x Posição X do botão.
     * @param y Posição Y do botão.
     * @param acao Ação a ser executada ao clicar no botão.
     * @return O JButton criado.
     */
    private JButton criarBotao(String texto, int x, int y, ActionListener acao) {
        JButton botao = new JButton(texto);
        botao.setBounds(x, y, 105, 27);
        botao.addActionListener(acao);
        getContentPane().add(botao);
        return botao;
    }

    /**
     * Configura o campo de preço com formatação específica.
     * @param campoPreco O campo de preço a ser configurado.
     */
    private void configurarCampoPreco(JFormattedTextField campoPreco) {
        DecimalFormat precoFormat = new DecimalFormat("R$ #,##0.00");
        NumberFormatter precoFormatter = new NumberFormatter(precoFormat);
        precoFormatter.setFormat(precoFormat);
        precoFormatter.setAllowsInvalid(false);
        precoFormatter.setMinimum(0.0);
        precoFormatter.setMaximum(999999.99);
        precoFormatter.setValueClass(Double.class);
        campoPreco.setFormatterFactory(new DefaultFormatterFactory(precoFormatter));
        campoPreco.setValue(0.0);
        campoPreco.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    /**
     * Configura os filtros para os campos de texto.
     */
    private void configurarFiltrosCampos() {
        configurarFiltroCampoNumerico(id);
        configurarFiltroCampoTexto(nome);
        configurarFiltroCampoNumerico(estoque);
    }

    /**
     * Configura um filtro para permitir apenas a entrada de números em um campo de texto.
     * @param campo O campo de texto a ser filtrado.
     */
    private void configurarFiltroCampoNumerico(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    /**
     * Configura um filtro para permitir apenas a entrada de texto em um campo de texto.
     * @param campo O campo de texto a ser filtrado.
     */
    private void configurarFiltroCampoTexto(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[a-zA-ZÀ-ú\\s-]+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[a-zA-ZÀ-ú\\s-]+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    /**
     * Ação a ser executada ao clicar no botão de pesquisar.
     */
    protected void onClickPesquisar() {
        try {
            String idText = id.getText().trim();
            validarId(idText);
            Long produtoId = Long.parseLong(idText);
            Produto produto = service.buscarPorId(produtoId);
            preencherCamposProduto(produto);
        } catch (Exception e) {
            mostrarMensagemErro("Erro ao pesquisar produto: " + e.getMessage());
        }
    }

    /**
     * Valida o ID do produto.
     * @param idText O texto do ID a ser validado.
     */
    private void validarId(String idText) {
        if (!idText.matches("\\d+")) {
            mostrarMensagemErro("ID deve conter apenas números");
            throw new IllegalArgumentException("ID inválido");
        }
    }

    /**
     * Preenche os campos do formulário com os dados do produto.
     * @param produto O produto a ser exibido.
     */
    private void preencherCamposProduto(Produto produto) {
        if (produto != null) {
            nome.setText(produto.getNome());
            medida.setSelectedItem(produto.getMedida());
            preco.setValue(produto.getPreco());
            estoque.setText(String.valueOf(produto.getEstoque()));
            cbTemEstoque.setEnabled(true);
            cbTemEstoque.setSelected(produto.getEstoque() > 0);
            estoque.setEnabled(cbTemEstoque.isSelected());
            habilitarCamposEdicao(true);
            btSalvar.setEnabled(true);
            btVoltar.setEnabled(true);
        } else {
            mostrarMensagemAviso("Produto não encontrado");
            limparCampos();
            setupConsultar();
        }
    }

    /**
     * Ação a ser executada ao clicar no botão de incluir novo produto.
     */
    protected void onClickIncluirNovoProduto() {
        limparCampos();
        id.setEnabled(false);
        cbTemEstoque.setEnabled(true);
        habilitarCamposEdicao(true);
        btSalvar.setEnabled(true);
        btVoltar.setEnabled(true);
        btPesquisar.setEnabled(false);
        btNovoProduto.setEnabled(false);
    }

    /**
     * Ação a ser executada ao clicar no botão de voltar.
     */
    protected void onClickVoltar() {
        limparCampos();
        setupConsultar();
    }

    /**
     * Ação a ser executada ao clicar no botão de salvar.
     */
    protected void onClickSalvar() {
        try {
            validarCampos();
            Produto produto = criarProduto();
            if (id.isEnabled()) {
                service.atualizar(produto);
                mostrarMensagemSucesso("Produto atualizado com sucesso!");
            } else {
                service.salvar(produto);
                mostrarMensagemSucesso("Produto cadastrado com sucesso!");
            }
            limparCampos();
            setupConsultar();
        } catch (Exception e) {
            mostrarMensagemErro("Erro ao salvar produto: " + e.getMessage());
        }
    }

    /**
     * Valida os campos do formulário.
     */
    private void validarCampos() {
        if (nome.getText().trim().isEmpty()) {
            mostrarMensagemErro("Nome é obrigatório");
            nome.requestFocus();
            throw new IllegalArgumentException("Nome inválido");
        }
        if (medida.getSelectedItem() == null) {
            mostrarMensagemErro("Selecione uma unidade de medida");
            medida.requestFocus();
            throw new IllegalArgumentException("Unidade de medida inválida");
        }
        validarPreco();
        validarEstoque();
    }

    /**
     * Valida o preço do produto.
     */
    private void validarPreco() {
        Double precoValue = ((Number) preco.getValue()).doubleValue();
        if (precoValue <= 0) {
            mostrarMensagemErro("Preço deve ser maior que zero");
            preco.requestFocus();
            throw new IllegalArgumentException("Preço inválido");
        }
    }

    /**
     * Valida a quantidade em estoque do produto.
     */
    private void validarEstoque() {
        Integer estoqueValue = Integer.parseInt(estoque.getText().trim());
        if (estoqueValue < 0) {
            mostrarMensagemErro("Estoque deve ser um número maior ou igual a zero");
            estoque.requestFocus();
            throw new IllegalArgumentException("Estoque inválido");
        }
    }

    /**
     * Cria um objeto Produto a partir dos dados do formulário.
     * @return O objeto Produto criado.
     */
    private Produto criarProduto() {
        String nomeText = nome.getText().trim();
        UnidadeMedida unidadeMedida = (UnidadeMedida) medida.getSelectedItem();
        Double precoValue = ((Number) preco.getValue()).doubleValue();
        Integer estoqueValue = Integer.parseInt(estoque.getText().trim());

        if (id.isEnabled()) {
            return new Produto(Long.parseLong(id.getText()), nomeText, unidadeMedida, precoValue, estoqueValue);
        } else {
            return new Produto(nomeText, unidadeMedida, precoValue, estoqueValue);
        }
    }

    /**
     * Configura a interface para o modo de consulta.
     */
    public void setupConsultar() {
        btSalvar.setEnabled(false);
        btVoltar.setEnabled(false);
        btNovoProduto.setEnabled(true);
        btPesquisar.setEnabled(true);
        cbTemEstoque.setEnabled(false);
        id.setEnabled(true);
        habilitarCamposEdicao(false);
    }

    /**
     * Limpa os campos do formulário.
     */
    private void limparCampos() {
        id.setText("");
        nome.setText("");
        medida.setSelectedIndex(0);
        preco.setValue(0.0);
        estoque.setText("0");
        cbTemEstoque.setSelected(false);
        estoque.setEnabled(false);
    }

    /**
     * Habilita ou desabilita os campos de edição do formulário.
     * @param habilitar Indica se os campos devem ser habilitados.
     */
    private void habilitarCamposEdicao(boolean habilitar) {
        nome.setEnabled(habilitar);
        medida.setEnabled(habilitar);
        preco.setEnabled(habilitar);
        estoque.setEnabled(habilitar && cbTemEstoque.isSelected());
    }

    /**
     * Exibe uma mensagem de erro.
     * @param mensagem A mensagem a ser exibida.
     */
    private void mostrarMensagemErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Exibe uma mensagem de aviso.
     * @param mensagem A mensagem a ser exibida.
     */
    private void mostrarMensagemAviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Exibe uma mensagem de sucesso.
     * @param mensagem A mensagem a ser exibida.
     */
    private void mostrarMensagemSucesso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
}

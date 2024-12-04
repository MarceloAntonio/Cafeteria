package cafeteria.clientes;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.MaskFormatter;

/**
 * Classe responsável pela visualização e interação.
 */
public class ClienteView extends JInternalFrame {

    private static final String TITULO = "Cadastro de Cliente";
    private static final int LARGURA = 580;
    private static final int ALTURA = 210;

    private JTextField id;
    private JTextField nome;
    private JFormattedTextField telefone;

    private JButton btSalvar;
    private JButton btVoltar;
    private JButton btNovoCliente;
    private JButton btPesquisar;

    private IClienteService service;
    private boolean modoEdicao = false;

    /**
     * Construtor da classe ClienteView.
     */
    public ClienteView(IClienteService service) {
        this.service = service;
        configurarJanela();
        inicializarComponentes();
        setupConsultar();
    }

    /**
     * Configura a janela interna.
     */
    private void configurarJanela() {
        setClosable(true);
        setIconifiable(true);
        setSize(LARGURA, ALTURA);
        setTitle(TITULO);
        getContentPane().setLayout(null);
    }

    /**
     * Inicializa os componentes da interface gráfica.
     */
    private void inicializarComponentes() {
        adicionarLabel("ID:", 31, 40);
        id = criarCampoTextoNumerico(109, 38);
        getContentPane().add(id);

        adicionarLabel("Nome:", 31, 73);
        nome = criarCampoTextoAlfabetico(109, 71);
        getContentPane().add(nome);

        adicionarLabel("Telefone:", 31, 106);
        telefone = criarCampoTelefone(109, 104);
        getContentPane().add(telefone);

        btSalvar = criarBotao("Salvar", 434, 126, e -> onClickSalvar());
        getContentPane().add(btSalvar);

        btVoltar = criarBotao("Voltar", 317, 126, e -> onClickVoltar());
        getContentPane().add(btVoltar);

        btNovoCliente = criarBotao("Novo Cliente", 400, 35, e -> onClickIncluirNovoCliente());
        getContentPane().add(btNovoCliente);

        btPesquisar = criarBotao("Pesquisar", 235, 35, e -> onClickPesquisar());
        getContentPane().add(btPesquisar);
    }

    /**
     * Adiciona um JLabel à interface.
     * @param texto Texto do JLabel.
     * @param x Posição X.
     * @param y Posição Y.
     */
    private void adicionarLabel(String texto, int x, int y) {
        JLabel label = new JLabel(texto);
        label.setBounds(x, y, 60, 17);
        getContentPane().add(label);
    }

    /**
     * Cria um campo de texto que aceita apenas números.
     * @param x Posição X.
     * @param y Posição Y.
     * @return JTextField configurado.
     */
    private JTextField criarCampoTextoNumerico(int x, int y) {
        JTextField campo = new JTextField() {
            @Override
            public void replaceSelection(String content) {
                if (content.matches("\\d*")) {
                    super.replaceSelection(content);
                }
            }
        };
        campo.setHorizontalAlignment(SwingConstants.CENTER);
        campo.setBounds(x, y, 114, 21);
        return campo;
    }

    /**
     * Cria um campo de texto que aceita apenas letras.
     * @param x Posição X.
     * @param y Posição Y.
     * @return JTextField configurado.
     */
    private JTextField criarCampoTextoAlfabetico(int x, int y) {
        JTextField campo = new JTextField() {
            @Override
            public void replaceSelection(String content) {
                if (content.matches("[a-zA-Z\\s]*")) {
                    super.replaceSelection(content);
                }
            }
        };
        campo.setBounds(x, y, 430, 21);
        return campo;
    }

    /**
     * Cria um campo de texto formatado para telefone.
     * @param x Posição X.
     * @param y Posição Y.
     * @return JFormattedTextField configurado.
     */
    private JFormattedTextField criarCampoTelefone(int x, int y) {
        try {
            MaskFormatter maskFormatter = new MaskFormatter("(##) #####-####");
            maskFormatter.setPlaceholderCharacter('_');
            JFormattedTextField campo = new JFormattedTextField(maskFormatter);
            campo.setBounds(x, y, 132, 21);
            return campo;
        } catch (Exception e) {
            e.printStackTrace();
            return new JFormattedTextField();
        }
    }

    /**
     * Cria um botão com ação associada.
     * @param texto Texto do botão.
     * @param x Posição X.
     * @param y Posição Y.
     * @param acao Ação a ser executada ao clicar.
     * @return JButton configurado.
     */
    private JButton criarBotao(String texto, int x, int y, java.awt.event.ActionListener acao) {
        JButton botao = new JButton(texto);
        botao.setBounds(x, y, 105, 27);
        botao.addActionListener(acao);
        return botao;
    }

    /**
     * Configura a tela para o modo de consulta.
     */
    public void setupConsultar() {
        modoEdicao = false;
        btSalvar.setEnabled(false);
        btVoltar.setEnabled(false);
        btNovoCliente.setEnabled(true);
        btPesquisar.setEnabled(true);

        id.setEnabled(true);
        nome.setEnabled(false);
        telefone.setEnabled(false);

        limparCampos();
    }

    /**
     * Ação ao clicar no botão Pesquisar.
     */
    protected void onClickPesquisar() {
        try {
            String idText = id.getText().trim();
            if (idText.isEmpty()) {
                mostrarMensagem("Por favor, informe um ID", "Aviso");
                return;
            }

            if (!idText.matches("\\d+")) {
                mostrarMensagem("ID deve conter apenas números", "Erro");
                return;
            }

            Long clienteId = Long.parseLong(idText);
            Cliente cliente = service.buscarPorId(clienteId);

            if (cliente != null) {
                modoEdicao = true;
                nome.setText(cliente.getNome());
                telefone.setText(cliente.getTelefone());
                
                nome.setEnabled(true);
                telefone.setEnabled(true);
                btSalvar.setEnabled(true);
                btVoltar.setEnabled(true);
                btNovoCliente.setEnabled(false);
                btPesquisar.setEnabled(false);
            } else {
                mostrarMensagem("Cliente não encontrado", "Aviso");
                limparCampos();
                setupConsultar();
            }
        } catch (Exception e) {
            mostrarMensagem("Erro ao pesquisar cliente: " + e.getMessage(), "Erro");
        }
    }

    /**
     * Ação ao clicar no botão Novo Cliente.
     */
    protected void onClickIncluirNovoCliente() {
        modoEdicao = false;
        limparCampos();
        
        id.setEnabled(false);
        nome.setEnabled(true);
        telefone.setEnabled(true);
        
        btSalvar.setEnabled(true);
        btVoltar.setEnabled(true);
        btPesquisar.setEnabled(false);
        btNovoCliente.setEnabled(false);
    }

    /**
     * Ação ao clicar no botão "Voltar".
     */
    protected void onClickVoltar() {
        setupConsultar();
    }

    /**
     * Ação ao clicar no botão Salvar.
     */
    protected void onClickSalvar() {
        try {
            String nomeText = nome.getText().trim();
            String telefoneText = telefone.getText().replace("(", "").replace(")", "")
                    .replace(" ", "").replace("-", "");

            // Validações
            if (nomeText.isEmpty()) {
                mostrarMensagem("Nome é obrigatório", "Aviso");
                return;
            }

            if (telefoneText.contains("_") || telefoneText.length() != 11) {
                mostrarMensagem("Telefone inválido", "Aviso");
                return;
            }

            Cliente cliente;
            if (modoEdicao) {
                cliente = new Cliente(Long.parseLong(id.getText()), nomeText, telefone.getText());
                service.atualizar(cliente);
                mostrarMensagem("Cliente atualizado com sucesso!", "Sucesso");
            } else {
                cliente = new Cliente(nomeText, telefone.getText());
                service.salvar(cliente);
                mostrarMensagem("Cliente cadastrado com sucesso!", "Sucesso");
            }

            setupConsultar();
        } catch (Exception e) {
            mostrarMensagem("Erro ao salvar cliente: " + e.getMessage(), "Erro");
        }
    }

    /**
     * Limpa os campos de entrada.
     */
    private void limparCampos() {
        id.setText("");
        nome.setText("");
        telefone.setText("");
    }

    /**
     * Exibe uma mensagem de diálogo.
     */
    private void mostrarMensagem(String mensagem, String titulo) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, JOptionPane.WARNING_MESSAGE);
    }
}
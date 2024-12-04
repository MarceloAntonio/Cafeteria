package cafeteria.relatorios;

import cafeteria.database.DatabaseConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class RelatorioView extends JInternalFrame {

    private static final int POSICAO_X_INICIAL = 90;
    private static final int POSICAO_Y_INICIAL = 90;

    private static final int LARGURA = 580;
    private static final int ALTURA = 190;

    private static final long serialVersionUID = 1L;

    private JTextField nomeRelatorio;
    private JTextField destinoCaminhoAbsoluto;
    private File destinoSelecionado = null;

    private JButton btExportar;
    private JButton btCancelar;
    private JButton btSelecionarDestino;

    private RelatorioExportavelEmArquivoTexto exportador = null;

    /**
     * Cria a janela para exportação do relatório
     */
    public RelatorioView(RelatorioExportavelEmArquivoTexto exportador) {
        this.exportador = exportador;


        setClosable(true);
        setIconifiable(true);
        setSize(LARGURA, ALTURA);
        setLocation(POSICAO_X_INICIAL, POSICAO_Y_INICIAL);
        setTitle("Exportador de Relatório");
        getContentPane().setLayout(null);

        JLabel lbId = new JLabel("Relatório:");
        lbId.setBounds(31, 40, 60, 17);
        getContentPane().add(lbId);

        nomeRelatorio = new JTextField();
        nomeRelatorio.setBounds(109, 38, 430, 21);
        getContentPane().add(nomeRelatorio);
        nomeRelatorio.setColumns(10);
        nomeRelatorio.setEditable(true);


        JLabel lbDestino = new JLabel("Destino:");
        lbDestino.setBounds(31, 73, 60, 17);
        getContentPane().add(lbDestino);

        destinoCaminhoAbsoluto = new JTextField();
        destinoCaminhoAbsoluto.setBounds(109, 71, 266, 21);
        getContentPane().add(destinoCaminhoAbsoluto);
        destinoCaminhoAbsoluto.setColumns(10);
        destinoCaminhoAbsoluto.setEditable(false);

        btExportar = new JButton("Exportar");
        btExportar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClickExportar();
            }
        });
        btExportar.setBounds(434, 107, 105, 27);
        getContentPane().add(btExportar);
        btExportar.setEnabled(false);

        btCancelar = new JButton("Cancelar");
        btCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClickCancelar();
            }
        });
        btCancelar.setBounds(316, 107, 105, 27);
        getContentPane().add(btCancelar);
        btCancelar.setEnabled(true);

        btSelecionarDestino = new JButton("Selecionar Destino");
        btSelecionarDestino.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClickSelecionarDestino();
            }
        });
        btSelecionarDestino.setBounds(387, 68, 152, 27);
        getContentPane().add(btSelecionarDestino);
        btSelecionarDestino.setEnabled(true);
    }

    /**
     * Executa as tarefas para efetuar uma pesquisa com base no ID informado
     */
    protected void onClickSelecionarDestino() {
        // Criação do JFileChooser
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // Obtendo o arquivo selecionado
            destinoSelecionado = fileChooser.getSelectedFile();
            destinoCaminhoAbsoluto.setText(destinoSelecionado.getAbsolutePath());
            btExportar.setEnabled(true);
        }
    }

    /**
     * Executa as tarefas para cancelar a geração do relatório
     */
    protected void onClickCancelar() {
        this.dispose();
    }

    /**
     * Executa as tarefas para exportar a exportação do relatório
     */
    protected void onClickExportar() {
        DatabaseConfig dbc = new DatabaseConfig();

        if (destinoSelecionado == null || nomeRelatorio.getText().trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Por favor, selecione um destino e insira o nome do relatório.",
                    "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nomeRelatorioTexto = nomeRelatorio.getText().trim();

        try (Connection conexao = DriverManager.getConnection(dbc.url, dbc.user, dbc.password)) {
            String sql = ""; // Consulta SQL definida com base no nomeRelatorioTexto

            // Define a consulta SQL com base no valor do campo
            if (nomeRelatorioTexto.equalsIgnoreCase("clientes")) {
                sql = "SELECT id, nome, telefone FROM cliente ORDER BY nome";
            } else if (nomeRelatorioTexto.equalsIgnoreCase("produtos")) {
                sql = "SELECT id, nome, medida, preco, estoque FROM produto ORDER BY nome";
            } else if (nomeRelatorioTexto.equalsIgnoreCase("melhores")) {
                sql = "SELECT v.cliente_id, c.nome AS cliente_nome, COUNT(v.cliente_id) AS frequencia FROM venda v JOIN cliente c ON v.cliente_id = c.id GROUP BY v.cliente_id, c.nome ORDER BY frequencia DESC";
            } else if (nomeRelatorioTexto.equalsIgnoreCase("dia")) {
                sql = "SELECT v.id AS venda_id, v.data_hora, v.cliente_id, c.nome AS cliente_nome FROM venda v JOIN cliente c ON v.cliente_id = c.id";
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Relatório desconhecido. Use 'clientes', 'produtos', 'melhores' ou 'dia'.",
                        "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Executa a consulta no banco de dados
            try (Statement stmt = conexao.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

                // Criação do XML
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.newDocument();

                // Elemento raiz
                Element rootElement = document.createElement("Relatorio");
                rootElement.setAttribute("tipo", nomeRelatorioTexto);
                document.appendChild(rootElement);

                // preenche o XML com os dados do banco
                while (rs.next()) {
                    Element item = document.createElement("Item");

                    if (nomeRelatorioTexto.equalsIgnoreCase("clientes")) {
                        item.appendChild(createElement(document, "ID", rs.getString("id")));
                        item.appendChild(createElement(document, "Nome", rs.getString("nome")));
                        item.appendChild(createElement(document, "Telefone", rs.getString("telefone")));
                    } else if (nomeRelatorioTexto.equalsIgnoreCase("produtos")) {
                        item.appendChild(createElement(document, "ID", rs.getString("id")));
                        item.appendChild(createElement(document, "Nome", rs.getString("nome")));
                        item.appendChild(createElement(document, "Medida", rs.getString("medida")));
                        item.appendChild(createElement(document, "Preco", rs.getString("preco")));
                        item.appendChild(createElement(document, "Estoque", rs.getString("estoque")));
                    } else if (nomeRelatorioTexto.equalsIgnoreCase("melhores")) {
                        item.appendChild(createElement(document, "ClienteID", rs.getString("cliente_id")));
                        item.appendChild(createElement(document, "Frequencia", rs.getString("frequencia")));
                        item.appendChild(createElement(document, "ClienteNome", rs.getString("cliente_nome")));
                    } else if (nomeRelatorioTexto.equalsIgnoreCase("dia")) {
                        item.appendChild(createElement(document, "VendaID", rs.getString("venda_id")));
                        item.appendChild(createElement(document, "DataHora", rs.getString("data_hora")));
                        item.appendChild(createElement(document, "ClienteID", rs.getString("cliente_id")));
                        item.appendChild(createElement(document, "ClienteNome", rs.getString("cliente_nome")));
                    }

                    rootElement.appendChild(item);
                }

                // Salva o XML no arquivo selecionado
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(new File(destinoSelecionado.getParent(), nomeRelatorioTexto + ".xml"));
                transformer.transform(source, result);

                javax.swing.JOptionPane.showMessageDialog(this,
                        "Relatório exportado com sucesso.",
                        "Sucesso", javax.swing.JOptionPane.INFORMATION_MESSAGE);

                this.dispose();
            }
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Erro ao exportar o relatório: " + e.getMessage(),
                    "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método auxiliar para criar elementos XML
    private Element createElement(Document document, String tagName, String textContent) {
        Element element = document.createElement(tagName);
        element.appendChild(document.createTextNode(textContent));
        return element;
    }


}



package cafeteria.vendas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import cafeteria.database.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * Classe responsável por gerenciar as operações de venda no sistema.
 * Implementa a interface IVendaService.
 */
public class VendaService implements IVendaService {
    static DatabaseConfig dbc = new DatabaseConfig();
    @Override
    public void registrarVenda(Venda venda) {
        validarVenda(venda); // Validações da venda

        try (Connection conn = DriverManager.getConnection(dbc.url, dbc.user, dbc.password)) {
            conn.setAutoCommit(false); // Inicia transação

            Long vendaId = inserirVenda(conn, venda); // Insere a venda e obtém o ID
            inserirItensVenda(conn, venda, vendaId); // Insere os itens da venda

            conn.commit(); // Confirma a transação
        } catch (Exception e) {
            tratarErroTransacao(e);
        }
    }

    private void validarVenda(Venda venda) {
        if (venda.getClienteId() == null) {
            throw new IllegalArgumentException("Cliente é obrigatório");
        }
        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new IllegalArgumentException("Venda deve ter pelo menos um item");
        }
    }

    private Long inserirVenda(Connection conn, Venda venda) throws Exception {
        String sqlVenda = "INSERT INTO venda (data_hora, cliente_id, desconto) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(venda.getDataHora()));
            stmt.setLong(2, venda.getClienteId());
            stmt.setDouble(3, venda.getDesconto());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                venda.setId(rs.getLong(1));
                return venda.getId();
            }
        }
        return null;
    }

    private void inserirItensVenda(Connection conn, Venda venda, Long vendaId) throws Exception {
        String sqlItem = "INSERT INTO item_venda (nome, medida, quantidade, preco, venda_id) VALUES (?, ?, ?, ?, ?)";
        String sqlEstoque = "UPDATE produto SET estoque = estoque - ? WHERE nome = ?"; // Atualiza o estoque

        try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem);
             PreparedStatement stmtEstoque = conn.prepareStatement(sqlEstoque)) {
            for (ItemVenda item : venda.getItens()) {
                validarItemVenda(item); // Valida o item da venda
                inserirItemVenda(stmtItem, item, vendaId); // Insere o item da venda
                atualizarEstoque(stmtEstoque, item); // Atualiza o estoque
            }
        }
    }

    private void validarItemVenda(ItemVenda item) {
        String nomeProduto = item.getNome().trim(); // Removendo espaços em branco
        if (nomeProduto.isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        }
    }

    private void inserirItemVenda(PreparedStatement stmtItem, ItemVenda item, Long vendaId) throws Exception {
        stmtItem.setString(1, item.getNome().trim());
        stmtItem.setInt(2, item.getMedida());
        stmtItem.setInt(3, item.getQuantidade());
        stmtItem.setDouble(4, item.getPreco());
        stmtItem.setLong(5, vendaId);
        stmtItem.executeUpdate();
    }

    private void atualizarEstoque(PreparedStatement stmtEstoque, ItemVenda item) throws Exception {
        stmtEstoque.setInt(1, item.getQuantidade());
        stmtEstoque.setString(2, item.getNome().trim());
        stmtEstoque.executeUpdate();
    }

    private void tratarErroTransacao(Exception e) {
        throw new RuntimeException("Erro ao registrar venda: " + e.getMessage());
    }

    @Override
    public Venda buscarPorId(Long id) {
        String sql = "SELECT v.id, v.data_hora, v.cliente_id, v.desconto, " +
                     "i.id as item_id, i.nome, i.medida, i.quantidade, i.preco " +
                     "FROM venda v " +
                     "LEFT JOIN item_venda i ON v.id = i.venda_id " +
                     "WHERE v.id = ?";

        try (Connection conn = DriverManager.getConnection(dbc.url, dbc.user, dbc.password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            return construirVenda(rs); // Constrói a venda a partir do ResultSet
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar venda: " + e.getMessage());
        }
    }

    private Venda construirVenda(ResultSet rs) throws Exception {
        Venda venda = null;
        while (rs.next()) {
            if (venda == null) {
                venda = new Venda();
                venda.setId(rs.getLong("id"));
                venda.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
                venda.setClienteId(rs.getLong("cliente_id"));
                venda.setDesconto(rs.getDouble("desconto"));
            }

            Long itemId = rs.getLong("item_id");
            if (!rs.wasNull()) {
                ItemVenda item = new ItemVenda();
                item.setId(itemId);
                item.setNome(rs.getString("nome").trim());
                item.setMedida(rs.getInt("medida"));
                item.setQuantidade(rs.getInt("quantidade"));
                item.setPreco(rs.getDouble("preco"));
                item.setVendaId(venda.getId());
                venda.getItens().add(item);
            }
        }
        return venda;
    }

    @Override
    public void cancelarVenda(Long id) {
        String sql = "DELETE FROM venda WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(dbc.url, dbc.user, dbc.password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Venda não encontrada");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao cancelar venda: " + e.getMessage());
        }
    }
}
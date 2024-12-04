package cafeteria.produtos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import cafeteria.database.DatabaseConfig;
import java.sql.DriverManager;


// Classe responsável pela lógica de negócios relacionada aos produtos.
public class ProdutoService implements IProdutoService {
    static DatabaseConfig dbc = new DatabaseConfig();
    @Override
    public Produto buscarPorId(Long id) {
        String sql = "SELECT id, nome, medida, preco, estoque FROM produto WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(dbc.url, dbc.user, dbc.password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetProduto(rs);
            }
            return null;
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto: " + e.getMessage());
        }
    }

    @Override
    public void salvar(Produto produto) {
        verificarProduto(produto);
        
        String sql = "INSERT INTO produto (nome, medida, preco, estoque) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(dbc.url, dbc.user, dbc.password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, produto.getNome());
            stmt.setInt(2, produto.getMedida().ordinal());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getEstoque());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar produto: " + e.getMessage());
        }
    }

    @Override
    public void atualizar(Produto produto) {
        verificarProduto(produto);
        
        String sql = "UPDATE produto SET nome = ?, medida = ?, preco = ?, estoque = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(dbc.url, dbc.user, dbc.password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, produto.getNome());
            stmt.setInt(2, produto.getMedida().ordinal());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getEstoque());
            stmt.setLong(5, produto.getId());
            
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Produto não encontrado para atualização");
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage());
        }
    }

    @Override
    public List<Produto> listarTodos() {
        String sql = "SELECT id, nome, medida, preco, estoque FROM produto ORDER BY nome";
        List<Produto> produtos = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(dbc.url, dbc.user, dbc.password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                produtos.add(mapResultSetProduto(rs));
            }
            return produtos;
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage());
        }
    }

    // Mapeia um ResultSet para um objeto Produto.
    private Produto mapResultSetProduto(ResultSet rs) throws SQLException {
        return new Produto(
            rs.getLong("id"),
            rs.getString("nome"),
            UnidadeMedida.fromOrdinal(rs.getInt("medida")),
            rs.getDouble("preco"),
            rs.getInt("estoque")
        );
    }

   
    // Verifica se
    private void verificarProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (produto.getMedida() == null) {
            throw new IllegalArgumentException("Unidade de medida é obrigatória");
        }
        
        if (produto.getPreco() == null || produto.getPreco() <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
        
        if (produto.getEstoque() == null || produto.getEstoque() < 0) {
            throw new IllegalArgumentException("Estoque não pode ser negativo");
        }
    }
}

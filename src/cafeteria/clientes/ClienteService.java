package cafeteria.clientes;

import cafeteria.database.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pela lógica relacional com os clientes.
 */
public class ClienteService implements IClienteService {

    static DatabaseConfig dbc = new DatabaseConfig();


    @Override
    public Cliente buscarPorId(Long id) {
        String sql = "SELECT id, nome, telefone FROM cliente WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(dbc.url, dbc.user, dbc.password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCliente(rs);
            }
            throw new RuntimeException("Cliente não encontrado com o ID: " + id);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public void salvar(Cliente cliente) {
        validarCliente(cliente);
        String sql = "INSERT INTO cliente (nome, telefone) VALUES (?, ?)";
        executeUpdate(cliente, sql);
    }

    @Override
    public void atualizar(Cliente cliente) {
        validarCliente(cliente);
        String sql = "UPDATE cliente SET nome = ?, telefone = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(dbc.url, dbc.user, dbc.password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.setLong(3, cliente.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Cliente não encontrado para atualização com o ID: " + cliente.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        String sql = "SELECT id, nome, telefone FROM cliente ORDER BY nome";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbc.url, dbc.user, dbc.password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes: " + e.getMessage(), e);
        }
        return clientes;
    }

    /**
     * Mapeia um ResultSet para um objeto Cliente.
     *
     * @param rs ResultSet contendo os dados do cliente.
     * @return Cliente mapeado.
     * @throws SQLException se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("telefone")
        );
    }

    /**
     * Classe para validar se o Nome do cliente esta vazio ou não
     */
    private void validarCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

    }

    /**
     * Executa uma atualização no banco de dados.
     *.
     */
    private void executeUpdate(Cliente cliente, String sql) {
        try (Connection conn = DriverManager.getConnection(dbc.url, dbc.user, dbc.password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar: " + e.getMessage(), e);
        }
    }
}
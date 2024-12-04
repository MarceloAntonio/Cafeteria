package cafeteria.clientes;

import java.util.List;

/**
 * Interface que define os serviços relacionados à manipulação de clientes.
 */
public interface IClienteService {

    /**
     * Busca um cliente pelo seu ID.
     */
    Cliente buscarPorId(Long id);

    /**
     * Salva um novo cliente
     */
    void salvar(Cliente cliente);

    /**
     * Atualiza as informações de um cliente existente.
     */
    void atualizar(Cliente cliente);

    /**
     * Lista todos os clientes cadastrados.
     */
    List<Cliente> listarTodos();
}
package cafeteria.produtos;

import java.util.List;

// Interface que para os produtos.
public interface IProdutoService {
    Produto buscarPorId(Long id);
    void salvar(Produto produto);
    void atualizar(Produto produto);
    List<Produto> listarTodos();
}

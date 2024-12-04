package cafeteria.vendas;

// Interface que define os serviços relacionados às operações de venda.

public interface IVendaService {
    void registrarVenda(Venda venda); // Registra uma nova venda no sistema.
    Venda buscarPorId(Long id); // Busca uma venda pelo seu identificador único.
    void cancelarVenda(Long id); // Cancela uma venda existente.
}
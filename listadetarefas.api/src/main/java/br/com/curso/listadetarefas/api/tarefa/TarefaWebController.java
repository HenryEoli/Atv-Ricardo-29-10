package br.com.curso.listadetarefas.api.tarefa;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/") // Responderá na raiz da aplicação: http://localhost:8080/
public class TarefaWebController {

    private final TarefaService tarefaService;

    public TarefaWebController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @GetMapping
    public String index(Model model) {
        // Buscamos todas as tarefas e as ordenamos por ID para consistência
        var tarefasOrdenadas = tarefaService.listarTodas()
                .stream()
                .sorted(Comparator.comparing(Tarefa::getId))
                .collect(Collectors.toList());

        // Adicionamos a lista de tarefas ao modelo que será enviado para o Thymeleaf
        model.addAttribute("tarefas", tarefasOrdenadas);

        // Retorna o nome do arquivo HTML (sem a extensão .html)
        return "index";
    }

    @PostMapping("/tarefas")
    public String criarTarefa(@RequestParam String titulo, Model model) {
        // Cria e salva a nova tarefa
        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setTitulo(titulo);
        novaTarefa.setDescricao(""); // Pode ser deixado em branco ou vir do form
        novaTarefa.setConcluida(false);
        Tarefa tarefaSalva = tarefaService.criarTarefa(novaTarefa);

        // Prepara o modelo APENAS com a nova tarefa
        model.addAttribute("tarefa", tarefaSalva);

        // Retorna o caminho para o FRAGMENTO, não a página inteira
        return "fragments :: linha-tarefa";

    }

    @DeleteMapping("/web/tarefas/{id}")
    @ResponseBody // Indica que não estamos retornando uma view, mas sim dados (ou nada) no corpo
    public ResponseEntity<Void> deletarTarefa(@PathVariable Long id) {
        tarefaService.deletarTarefa(id);
        // Retorna HTTP 200 OK. HTMX entende que uma resposta vazia e bem-sucedida
        // significa que o elemento alvo deve ser removido (se hx-swap for outerHTML).
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tarefas/{id}/toggle")
    public String toggleTarefaConcluida(@PathVariable Long id, Model model) {
        // Busca a tarefa no banco
        tarefaService.findById(id).ifPresent(tarefa -> {
            // Inverte o estado de 'concluida'
            tarefa.setConcluida(!tarefa.isConcluida());
            // Salva a tarefa atualizada
            Tarefa tarefaAtualizada = tarefaService.atualizarTarefa(id, tarefa).orElse(tarefa);
            // Adiciona ao modelo para enviar de volta ao fragmento
            model.addAttribute("tarefa", tarefaAtualizada);
        });

        // Retorna o fragmento da linha atualizado
        return "fragments :: linha-tarefa";
    }

    @PostMapping("/tarefas/{id}/toggle")
    public String toggleTarefaConcluida(@PathVariable Long id, Model model) {
        // Busca a tarefa no banco
        tarefaService.findById(id).ifPresent(tarefa -> {
            // Inverte o estado de 'concluida'
            tarefa.setConcluida(!tarefa.isConcluida());
            // Salva a tarefa atualizada
            Tarefa tarefaAtualizada = tarefaService.atualizarTarefa(id, tarefa).orElse(tarefa);
            // Adiciona ao modelo para enviar de volta ao fragmento
            model.addAttribute("tarefa", tarefaAtualizada);
        });

        // Retorna o fragmento da linha atualizado
        return "fragments :: linha-tarefa";
    }
}
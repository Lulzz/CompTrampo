// JavaScript para rolagem suave nas seções
document.querySelectorAll('.navbar .opcao').forEach(link => {
    link.addEventListener('click', function (e) {
        e.preventDefault(); // Impede a navegação padrão (pular diretamente para a seção)

        const targetId = this.getAttribute('href').substring(1); // Obtém o id da seção de destino
        const targetSection = document.getElementById(targetId); // Localiza a seção pelo id

        // Anima a rolagem suave até a seção
        targetSection.scrollIntoView({
            behavior: 'smooth', // Rolagem suave
            block: 'start' // Inicia a rolagem no topo da seção
        });
    });
});

const boxes = document.getElementsByClassName('box');
let contador = 0;

function atualizarBoxes() {
    // Remove a classe 'box-active' de todos os elementos
    for (let i = 0; i < boxes.length; i++) {
        boxes[i].classList.remove('box-active');
    }

    // Adiciona a classe 'box-active' aos elementos atuais
        boxes[contador].classList.add('box-active');    
        boxes[contador + 1].classList.add('box-active');   
}

function atualizarContador() {
    // Atualiza o contador para os próximos elementos
    if (contador + 2 < boxes.length) {
        contador += 2;
    } else {
        // Reinicia o contador se atingir o final da lista
        contador = 0;
    }
}

function iniciarAnimacao() {
    atualizarBoxes();
    atualizarContador();
}

// Inicia o ciclo a cada 2.5 segundos ou 2500 milissegundos
setInterval(iniciarAnimacao, 2500);

// Executa a função imediatamente na primeira vez
iniciarAnimacao();





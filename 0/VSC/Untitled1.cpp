#include<stdio.h>

struct pessoa{ //nomde da variavel 
	char nome[30]; //ARMAZENA AS VARIAVEIS  
	int idade;
};

main()
{        //nome   vetor de 3 amazens 
	struct pessoa p[3];
	int i; // variavel i pra armazenar P[]
	
	for(i=0;i<3;i++){ //repete 3 vezes 
	
		printf("Pessoa %d\n", i+1);// improme um cabesalho pessoa 1   pessoa 2 pessoa 3
		
		do{
			printf("informe a idade: ");
			scanf("%d", &p[i].idade);
		}
		while(p[i].idade>45);
		
		printf("infome seu nome: "); 
		fflush(stdin);
		gets(p[i].nome);
	}
	
	for(i=0;i<3;i++){
		printf("nome: %s\n", p[i].nome);
		printf("\nidade: %d\n\n", p[i].idade);
	}
}

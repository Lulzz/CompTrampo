#include<stdio.h>

float func(float notas[3], float *ma, float *me);

main()
{
	int i;
	float n[3], menor=999, maior=-999, media;
	for(i=0;i<3;i++)
	{
		printf("Informa a nota: "); scanf("%f", &n[i]);
	}
	media=func(n, &maior, &menor);
	printf("A menor nota foi: %f\n", menor);
	printf("A maior nota foi: %f\n", maior);
	printf("A media foi: %f\n", media);
}

float func(float notas[3], float *ma, float *me)
{
	int i;
	float soma=0;
	for(i=0;i<3;i++)
	{
		if(notas[i]>*ma)
		{
			*ma=notas[i];
		}
		if(notas[i]<*me)
		{
			*me=notas[i];
		}
		soma=soma+notas[i];
	}
	return soma/3;
}
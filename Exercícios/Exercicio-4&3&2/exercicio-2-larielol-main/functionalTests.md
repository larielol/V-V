# Functional Tests - BankAccountManager

## Estratégia de Testes Funcionais (Caixa-Preta)

planejamento da suítes de testes funcionais para o sistema BankAccountManager, aplicando técnicas de teste de caixa-preta baseadas exclusivamente na especificação do sistema.

---

## 1. Funcionalidade: Criar Conta

**Técnica Aplicada:** Análise de Valores Limite e Partições de Equivalência

| Caso | Número Conta | Titular | Tipo Conta | Saldo Inicial | Resultado Esperado | Justificativa |
|------|-------------|---------|------------|---------------|-------------------|---------------|
| 1    | "001"       | "João"  | CORRENTE   | -0.01         | ERRO              | Valor abaixo do limite inferior |
| 2    | "002"       | "Maria" | CORRENTE   | 0.00          | OK                | Valor no limite inferior válido |
| 3    | "003"       | "Pedro" | CORRENTE   | 0.01          | OK                | Valor logo acima do limite inferior |
| 4    | "004"       | "Ana"   | CORRENTE   | 1000.00       | OK                | Valor típico no meio do intervalo |
| 5    | "005"       | "Carlos"| POUPANCA   | 1000.00       | OK                | Valor típico para poupança |
| 6    | "006"       | "Sofia" | null       | 100.00        | ERRO              | Tipo de conta inválido |
| 7    | "001"       | "João2" | CORRENTE   | 100.00        | ERRO              | Número de conta duplicado |

**Classes de Equivalência:**
- Saldo inicial: negativo (inválido), zero (válido), positivo (válido)
- Tipo de conta: CORRENTE (válido), POUPANCA (válido), outros (inválido)
- Número da conta: único (válido), duplicado (inválido)

---

## 2. Funcionalidade: Depósito

**Técnica Aplicada:** Análise de Valores Limite

| Caso | Valor do Depósito | Resultado Esperado | Justificativa |
|------|-------------------|-------------------|---------------|
| 1    | -50.00           | ERRO              | Valor negativo inválido |
| 2    | -0.01            | ERRO              | Valor logo abaixo do limite |
| 3    | 0.00             | ERRO              | Valor zero (inválido) |
| 4    | 0.01             | OK                | Valor logo acima do limite |
| 5    | 100.00           | OK                | Valor típico no meio do intervalo |
| 6    | 10000.00         | OK                | Valor grande (sem limite superior) |

---

## 3. Funcionalidade: Saque

**Técnica Aplicada:** Tabela de Decisão

**Condições:**
1. C1: Valor do saque > 0?
2. C2: Saldo suficiente? (Para conta corrente: saldo + limite de cheque especial)
3. C3: Tipo de conta = CORRENTE?
4. C4: Para conta corrente, valor ≤ saldo + limite especial (até R$1000)?

**Ações:**
- A1: ERRO - Valor inválido
- A2: ERRO - Saldo insuficiente (poupança)
- A3: ERRO - Excede limite do cheque especial
- A4: OK - Saque realizado com sucesso
- A5: OK - Saque usando cheque especial (dentro do limite)

| C1  | C2  | C3  | C4  | Ação | Caso de Teste |
|-----|-----|-----|-----|------|---------------|
| F   | -   | -   | -   | A1   | Saque com valor ≤ 0 |
| T   | F   | F   | -   | A2   | Poupança sem saldo |
| T   | F   | T   | F   | A3   | Corrente excede limite especial |
| T   | F   | T   | T   | A5   | Corrente usa cheque especial |
| T   | T   | T   | T   | A4   | Corrente com saldo suficiente |
| T   | T   | F   | -   | A4   | Poupança com saldo suficiente |

**Exemplos concretos:**
| Caso | Tipo Conta | Saldo | Valor Saque | Limite Especial | Resultado Esperado |
|------|------------|-------|-------------|-----------------|-------------------|
| 1    | POUPANCA   | 50.00 | 60.00       | 0               | ERRO              |
| 2    | POUPANCA   | 100.00| 50.00       | 0               | OK                |
| 3    | CORRENTE   | 50.00 | 60.00       | 100             | OK                |
| 4    | CORRENTE   | 50.00 | 200.00      | 100             | ERRO              |
| 5    | CORRENTE   | 100.00| 50.00       | 100             | OK                |

---

## 4. Funcionalidade: Transferência

**Técnica Aplicada:** Tabela de Decisão

**Condições:**
1. C1: Conta origem existe?
2. C2: Conta destino existe?
3. C3: Valor transferência > 0?
4. C4: Saldo origem suficiente (considerando cheque especial)?

**Ações:**
- A1: ERRO - Conta origem/destino não existe
- A2: ERRO - Valor inválido
- A3: ERRO - Saldo insuficiente
- A4: OK - Transferência realizada

| C1  | C2  | C3  | C4  | Ação | Caso de Teste |
|-----|-----|-----|-----|------|---------------|
| F   | -   | -   | -   | A1   | Conta origem não existe |
| T   | F   | -   | -   | A1   | Conta destino não existe |
| T   | T   | F   | -   | A2   | Valor transferência ≤ 0 |
| T   | T   | T   | F   | A3   | Saldo origem insuficiente |
| T   | T   | T   | T   | A4   | Transferência bem-sucedida |

---

## 5. Funcionalidade: Aprovar Limite de Cheque Especial

**Técnica Aplicada:** Análise de Valores Limite

| Caso | Tipo Conta | Novo Limite | Resultado Esperado | Justificativa |
|------|------------|-------------|-------------------|---------------|
| 1    | POUPANCA   | 500.00      | ERRO              | Poupança não pode ter cheque especial |
| 2    | CORRENTE   | -0.01       | ERRO              | Limite negativo inválido |
| 3    | CORRENTE   | 0.00        | OK                | Limite zero válido |
| 4    | CORRENTE   | 0.01        | OK                | Valor logo acima do limite inferior |
| 5    | CORRENTE   | 500.00      | OK                | Valor típico no meio do intervalo |
| 6    | CORRENTE   | 1000.00     | OK                | Valor no limite superior válido |
| 7    | CORRENTE   | 1000.01     | ERRO              | Valor acima do limite superior |

---

## 6. Funcionalidade: Extrato

**Técnica Aplicada:** Partições de Equivalência

*Justificativa: Não aplicamos Análise de Valores Limite porque datas não têm limites numéricos fixos, nem Tabela de Decisão porque não há múltiplas condições booleanas interagindo.*

**Classes de Equivalência:**
1. Período com transações
2. Período sem transações
3. Período que inclui algumas transações
4. Extrato completo (sem período)

**Casos de Teste:**
| Caso | Período Especificado | Transações no Período | Resultado Esperado |
|------|----------------------|-----------------------|-------------------|
| 1    | Não                  | 5 transações          | Retorna todas as 5 transações |
| 2    | Sim                  | 0 transações          | Retorna lista vazia |
| 3    | Sim                  | 3 transações          | Retorna as 3 transações |
| 4    | Sim                  | 2 de 5 transações     | Retorna apenas as 2 transações |

---

## 7. Resultados dos Testes:

## 1️⃣ Funcionalidade: Criar Conta

Técnica Aplicada: Análise de Valores Limite

Eu escolhi essa técnica porque a regra mais clara para criar conta era sobre o saldo inicial: ele não pode ser negativo. Então, testei os valores bem no limite dessa regra.
| #	| Saldo Inicial	| Tipo de Conta	| Resultado Esperado | Resultado Obtido | Status |
|---|---------------|---------------|--------------------|------------------|--------|
| 1	|-0.01          | CORRENTE	    | ❌ Erro	           | ❌ Erro	        | ✅ Passou |
| 2	| 0.00	        | CORRENTE	    | ✅ OK	             | ✅ OK	          | ✅ Passou |
| 3	| 0.01	        | CORRENTE	    | ✅ OK	             | ✅ OK	          | ✅ Passou |
| 4	| 1000.00	      | CORRENTE	    | ✅ OK	             | ✅ OK	          | ✅ Passou |
| 5	| 1000.00	      | POUPANCA	    | ✅ OK	             | ✅ OK	          | ✅ Passou |

O que eu testei:

    Os casos 1, 2 e 3 são os valores limite em torno do zero (-0.01, 0.00, 0.01). A regra diz "não pode ser negativo", então -0.01 deve falhar e 0.00 deve passar.

    Os casos 4 e 5 são valores normais no meio, só para confirmar que tipos diferentes de conta também funcionam.

    Um caso extra que testei (mas não coloquei na tabela) foi tentar criar uma conta com um número que já existe. Isso também deu erro, como esperado!

## 2️⃣ Funcionalidade: Depósito

Técnica Aplicada: Análise de Valores Limite

Para depósito, a regra era clara: só valores positivos. Então, testei os limites em torno do zero de novo, mas também valores bem altos para ver se tinha algum limite secreto.
| #	| Valor do Depósito	| Resultado Esperado	| Resultado Obtido	| Status |
|---|-------------------|---------------------|-------------------|--------|
| 1	| -50.00	          | ❌ Erro	            | ❌ Erro	          | ✅ Passou |
| 2	| -0.01	            | ❌ Erro	            | ❌ Erro	          | ✅ Passou |
| 3	| 0.00	            | ❌ Erro	            | ❌ Erro	          | ✅ Passou |
| 4	| 0.01	            | ✅ OK	              | ✅ OK	            | ✅ Passou |
| 5	| 100.00	          | ✅ OK	              | ✅ OK	            | ✅ Passou |
| 6	| 10000.00	        | ✅ OK	              | ✅ OK	            | ✅ Passou |

O que eu testei:

    Foquei nos limites do zero: -0.01, 0.00 e 0.01. A especificação não deixa claro se zero é permitido ou não, mas como diz "valores positivos", considerei que zero também não vale.

    Testei um valor bem alto (10 mil) para ver se o sistema impunha um limite máximo. Ele não impôs, então está de acordo com a especificação que não menciona teto para depósitos.

## 3️⃣ Funcionalidade: Saque

Técnica Aplicada: Tabela de Decisão

O saque tem várias regras misturadas: valor positivo, saldo suficiente, tipo de conta, limite do cheque especial... Fica confuso testar tudo separado. A Tabela de Decisão foi otima para organizar:

Primeiro, escrevi as condições que importam:

    C1: Valor do saque > 0?

    C2: Tem saldo suficiente? (Considerando cheque especial para corrente)

    C3: É conta CORRENTE?

    C4: Está dentro do limite extra de R$100? (Para correntes)

Depois, montei a tabela com as combinações e o que deveria acontecer em cada uma:

| C1 (Valor>0) | C2 (Saldo) | C3 (É Corrente?) | C4 (Dentro do Limite) | Ação Esperada	                          | Meu caso de teste |
|--------------|------------|------------------|-----------------------|------------------------------------------|-------------------|
| F	           | -	        | -	               | -	                   | ❌ Erro (Valor inválido)	                | Saque de R$ 0,00 |
| V	           | F	        | F	               | -	                   | ❌ Erro (Saldo insuficiente na Poupança)	| Poupança com R$50 tenta sacar R$60 |
| V	           | F	        | V	               | F	                   | ❌ Erro (Passou do cheque especial)	    | Corrente com R$50 tenta sacar R$151 |
| V	           | F	        | V	               | V	                   | ✅ OK (Usa cheque especial)	            | Corrente com R$50 saca R$60 (usa R$10 do limite) |
| V	           | V	        | V	               | V	                   | ✅ OK (Saque normal)	                    | Corrente com R$100 saca R$50 |
| V	           | V	        | F	               | -	                   | ✅ OK (Saque normal)	                    | Poupança com R$100 saca R$50 |

Resultado dos Testes: Todos passaram! O sistema se comportou exatamente como a tabela previa para cada combinação de regras.

## 4️⃣ Funcionalidade: Transferência

Técnica Aplicada: Tabela de Decisão

Transferência é um saque + um depósito, então as regras são parecidas, mas tem uma condição extra: as duas contas precisam existir.

Condições:

    C1: Conta origem existe?

    C2: Conta destino existe?

    C3: Valor > 0?

    C4: Origem tem saldo suficiente?

| C1 (Origem Existe?)	| C2 (Destino Existe?)	| C3 (Valor>0?)	| C4 (Saldo Suficiente?) | Ação Esperada	| Status do Teste |
|---------------------|-----------------------|---------------|------------------------|----------------|-----------------|
| F	                  | -	                    | -	            | -	                     | ❌ Erro	      | ✅ Passou       |
| V	                  | F	                    | -	            | -	                     | ❌ Erro	      | ✅ Passou |
| V	                  | V	                    | F	            | -	                     | ❌ Erro	      | ✅ Passou | 
| V	                  | V	                    | V	            | F	                     | ❌ Erro	      | ✅ Passou | 
| V	                  | V	                    | V	            | V	                     | ✅ OK	        | ✅ Passou |

Resultado:  Todos passaram também! Uma coisa legal que testei foi uma transferência onde a conta corrente precisou usar o cheque especial para completar o valor.

## 5️⃣ Funcionalidade: Aprovar Limite de Cheque Especial

Técnica Aplicada: Análise de Valores Limite

O limite tem regras claras: só para conta CORRENTE, e deve ser entre 0 e R$ 1000. Ideal para testar valores limite!
| #	| Tipo de Conta	| Novo Limite	| Resultado Esperado	| Resultado Obtido	| Status |
|---|---------------|-------------|---------------------|-------------------|--------|
| 1	| POUPANCA	    | 500.00	    | ❌ Erro (só para corrente)	| ❌ Erro	  | ✅ Passou |
| 2	| CORRENTE	    | -0.01	      | ❌ Erro (negativo)	| ❌ Erro	          | ✅ Passou |
| 3	| CORRENTE	    | 0.00	      | ✅ OK	              | ✅ OK	            | ✅ Passou |
| 4	| CORRENTE	    | 0.01	      | ✅ OK	              | ✅ OK	            | ✅ Passou |
| 5	| CORRENTE	    | 500.00	    | ✅ OK	              | ✅ OK	            | ✅ Passou |
| 6	| CORRENTE	    | 1000.00	    | ✅ OK	              | ✅ OK	            | ✅ Passou |
| 7	| CORRENTE	    | 1000.01	    | ❌ Erro (acima do máximo)	| ❌ Erro	    | ✅ Passou |

Resultado: Todos os casos nos limites (negativo, zero, máximo) funcionaram.
## 6️⃣ Funcionalidade: Extrato

Técnica Aplicada: Partições de Equivalência

Aqui a especificação pedia para testar se o extrato:

    Lista todas as transações

    Na ordem certa (mais antigas primeiro)

    Permite filtrar por período

Como datas não são números com limites fixos, não dava para usar Análise de Valores Limite. Em vez disso, pensei em partições: situações diferentes que podem acontecer.
| Situação de Teste	| O que Fiz	| Resultado Esperado	| Resultado Obtido	| Status |
|-------------------|-----------|---------------------|-------------------|--------|
| Extrato completo  | Criei conta, fiz 3 transações | Lista as 3 transações na ordem que fiz	| ✅ Listou as 3 na ordem correta	| ✅ Passou |
| Período SEM transações	| Pedi extrato de um período no futuro	| Lista vazia	| ✅ Lista vazia	| ✅ Passou |
| Conta NOVA (sem nenhuma transação)	| Só criei a conta	| Lista vazia	| ✅ Lista vazia	| ✅ Passou |

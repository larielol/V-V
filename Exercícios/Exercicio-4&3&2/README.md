[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/5nJn12Aa)
# Exercício 2 — Aplicando TDD: *BankAccountManager*

## Objetivo

Desenvolver individualmente um sistema de **gestão de contas bancárias em Java**, aplicando o ciclo **Test-Driven Development (TDD)** em todas as etapas.

O foco é **praticar o raciocínio incremental de testes e código**, refletindo sobre o papel dos testes no design do software.
Cada iteração deve seguir o ciclo **Red → Green → Refactor**.

---

## Como começar

1. Aceite o convite do GitHub Classroom e **clone o repositório** gerado a partir do template.
2. Crie e trabalhe em uma **nova branch chamada `tdd`**, para manter o histórico separado da branch principal:

   ```bash
   git checkout -b tdd
   ```
3. Implemente as funcionalidades descritas abaixo **exclusivamente via TDD**: primeiro o teste, depois o código.
4. Cada commit deve representar uma etapa do ciclo TDD:

   * **RED:** teste criado e falhando
   * **GREEN:** implementação mínima para passar o teste
   * **REFACTOR:** melhoria do código mantendo todos os testes passando
5. Use **JUnit** para os testes.
6. Ao finalizar, envie suas alterações:

   ```bash
   git push origin tdd
   ```

---

## Descrição do Sistema — *BankAccountManager*

O sistema deve permitir a **criação e manipulação de contas bancárias**, respeitando as regras de negócio definidas.
As funcionalidades devem ser implementadas **progressivamente**, de forma **iterativa via TDD**.

---

## Funcionalidades (User Stories)

### 1. Criar conta

**Como cliente do banco**, quero criar uma conta informando número, titular, tipo (CORRENTE ou POUPANCA) e saldo inicial, **para começar a movimentar meu dinheiro de forma segura**.

**Regras:**

* O número da conta deve ser único.
* O saldo inicial não pode ser negativo.
* O tipo de conta deve ser válido.

---

### 2. Depósito

**Como cliente**, quero depositar valores positivos na minha conta **para que meu saldo aumente corretamente e a transação seja registrada.**

**Regras:**

* Aceita apenas valores positivos.
* Atualiza o saldo da conta.
* Registra uma transação do tipo `DEPOSITO`.

---

### 3. Saque

**Como cliente**, quero sacar dinheiro da minha conta **para usar meus fundos conforme minhas necessidades.**

**Regras:**

* Só é permitido se houver saldo suficiente.
* Conta **CORRENTE** pode entrar até R$100,00 no cheque especial.
* Conta **POUPANCA** não pode ter saldo negativo.
* Registra uma transação do tipo `SAQUE`.

---

### 4. Transferência

**Como cliente**, quero transferir valores entre contas existentes **para enviar dinheiro para outras pessoas ou minhas próprias contas.**

**Regras:**

* Aplica as mesmas regras de saque e depósito.
* Registra duas transações: `TRANSFERENCIA_ENVIADA` e `TRANSFERENCIA_RECEBIDA`.

---

### 5. Extrato

**Como cliente**, quero consultar o extrato da minha conta **para visualizar todas as transações realizadas em ordem cronológica.**

**Regras:**

* Lista todas as transações da conta (depósitos, saques, transferências).
* Ordem cronológica (mais antigas primeiro).
* Permite filtro por período (ex.: últimos 30 dias).
* Exibe tipo, valor, data/hora e conta de origem/destino.

---

### 6. Limite de Cheque Especial

**Como gerente**, quero aprovar ou ajustar o limite de cheque especial de contas correntes **para dar mais flexibilidade aos clientes.**

**Regras:**

* Apenas contas **CORRENTE** podem ter cheque especial.
* O gerente define um limite máximo (até R$1000).
* Não permite limite negativo.
* Alterações registradas como **transações administrativas**.

---

## Ciclo TDD

1. **RED:** escrever um teste que falha.
2. **GREEN:** implementar o código mínimo para passar o teste.
3. **REFACTOR:** refatorar mantendo todos os testes passando.

💡 *Faça commits pequenos e frequentes, descrevendo a etapa (ex.: “RED: should not allow negative balance”).*

---

## Estrutura esperada

```
/src
  main/java/br/ufcg/bank/          → código de produção
  test/java/br/ufcg/bank/tddTests → testes unitários (JUnit)
TEST_PLAN.md                       → resumo dos testes implementados
README.md                          → instruções de execução
```

---

## TEST_PLAN.md

Preencha o arquivo `TEST_PLAN.md` conforme indicado, descrevendo **quais casos foram implementados** e a **estratégia usada**.

---
## Deadline

**19/11/2025**

---

## Entregáveis

O repositório deve conter:

* Código-fonte funcional.
* Testes unitários passando.
* Histórico de commits mostrando o ciclo TDD.
* Arquivo `TEST_PLAN.md` preenchido.
* Branch `tdd` criada.

Formulário de entrega:
👉 [https://forms.gle/JypT75QmVBpvtKfn9](https://forms.gle/JypT75QmVBpvtKfn9)

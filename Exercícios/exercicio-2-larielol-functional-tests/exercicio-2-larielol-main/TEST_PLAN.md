# TEST_PLAN.md — BankAccountManager

Este documento lista os testes unitários criados no exercício de TDD.  
O objetivo é garantir que você desenvolveu os testes, evitando cópias de código.

---

## Funcionalidades / Testes Implementados

Marque com ✅ os testes que você implementou:

| Funcionalidade                       | Teste Implementado |
|-------------------------------------|------------------|
| Criar conta                           |         ✅         |
| Depósito                              |         ✅         |
| Saque                                 |         ✅         |
| Transferência                         |         ✅         |
| Extrato                               |         ✅         |
| Aprovação de limite de cheque especial |         ✅         |

---

## Estratégia de Teste

Em 3–5 linhas, explique **em termos gerais** como você testou cada funcionalidade do sistema.  
Exemplo: quais tipos de entradas você considerou, se testou cenários válidos e inválidos, ou como garantiu que regras de negócio foram respeitadas.  

-  Criar conta: Testei criação com dados válidos e casos de erro como saldo inicial negativo. Validei que os campos são armazenados corretamente e as restrições são aplicadas.

-  Depósito: Testei valores positivos (sucesso) e valores negativos/zero (erro). Verifiquei se o saldo é atualizado e se a transação é registrada corretamente.

-  Saque: Testei saques com saldo suficiente e insuficiente, diferenciando entre conta corrente (com cheque especial) e poupança (sem saldo negativo). Validei limites do cheque especial.

-  Transferência: Testei transferências válidas e casos de saldo insuficiente. Verifiquei se ambas as contas têm suas transações registradas e se os saldos são atualizados corretamente.

-  Extrato: Testei a listagem de transações em ordem cronológica e a filtragem por período. Validei que transações são retornadas na ordem correta.

-  Aprovação de limite de cheque especial: Testei configuração de limite para conta corrente (sucesso) e casos de erro como limite negativo, acima do máximo, e tentativa em conta poupança.

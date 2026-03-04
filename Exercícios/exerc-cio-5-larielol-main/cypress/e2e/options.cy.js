describe('Seção de Opções de Produto - Admin Sylius', () => {
  beforeEach(() => {
    cy.visit('/admin/login')
    cy.get('input[name="_username"]').type('admin@example.com')
    cy.get('input[name="_password"]').type('sylius')
    cy.get('button[type="submit"]').click()
    cy.url().should('include', '/admin/dashboard')
    cy.visit('/admin/product-options/')
    cy.contains('Product options').should('be.visible')
  })

  it('CT001 - Criar opção de produto simples com sucesso', () => {
    cy.contains('Create').click()
    cy.get('#sylius_product_option_code').type('TAMANHO')
    cy.get('#sylius_product_option_translations_en_US_name').type('Tamanho')
    cy.contains('Add value').click()
    cy.get('input[placeholder="Code"]').last().type('P')
    cy.get('input[placeholder="Value"]').last().type('Pequeno')
    cy.contains('button', 'Create').click()
    cy.contains('Product option has been successfully created.').should('be.visible')
    cy.contains('TAMANHO').should('be.visible')
  })

  it('CT002 - Criar opção com múltiplos valores', () => {
    cy.contains('Create').click()
    cy.get('#sylius_product_option_code').type('COR')
    cy.get('#sylius_product_option_translations_en_US_name').type('Cor')
    
    cy.contains('Add value').click()
    cy.get('input[placeholder="Code"]').eq(0).type('VERMELHO')
    cy.get('input[placeholder="Value"]').eq(0).type('Vermelho')
    
    cy.contains('Add value').click()
    cy.get('input[placeholder="Code"]').eq(1).type('AZUL')
    cy.get('input[placeholder="Value"]').eq(1).type('Azul')
    
    cy.contains('Add value').click()
    cy.get('input[placeholder="Code"]').eq(2).type('VERDE')
    cy.get('input[placeholder="Value"]').eq(2).type('Verde')
    
    cy.contains('button', 'Create').click()
    cy.contains('Product option has been successfully created.').should('be.visible')
  })

  it('CT003 - Validar campos obrigatórios ao criar opção', () => {
    cy.contains('Create').click()
    cy.contains('button', 'Create').click()
    cy.contains('Please enter option code.').should('be.visible')
    cy.contains('Please enter option name.').should('be.visible')
    cy.get('#sylius_product_option_code').should('have.class', 'error')
    cy.get('#sylius_product_option_translations_en_US_name').should('have.class', 'error')
  })

  it('CT004 - Validar código duplicado', () => {
    cy.contains('Create').click()
    cy.get('#sylius_product_option_code').type('TAMANHO')
    cy.get('#sylius_product_option_translations_en_US_name').type('Tamanho Duplicado')
    cy.contains('Add value').click()
    cy.get('input[placeholder="Code"]').last().type('G')
    cy.get('input[placeholder="Value"]').last().type('Grande')
    cy.contains('button', 'Create').click()
    cy.contains('The option with given code already exists.').should('be.visible')
  })

  it('CT005 - Editar opção existente', () => {
    cy.get('input[type="search"]').type('TAMANHO')
    cy.contains('Search').click()
    cy.get('tbody tr').first().contains('Edit').click()
    cy.get('#sylius_product_option_translations_en_US_name').clear().type('Tamanho Editado')
    cy.contains('Save changes').click()
    cy.contains('Product option has been successfully updated.').should('be.visible')
    cy.contains('Tamanho Editado').should('be.visible')
  })

  it('CT006 - Adicionar novo valor a opção existente', () => {
    cy.get('input[type="search"]').type('TAMANHO')
    cy.contains('Search').click()
    cy.get('tbody tr').first().contains('Edit').click()
    cy.contains('Add value').click()
    cy.get('input[placeholder="Code"]').last().type('GG')
    cy.get('input[placeholder="Value"]').last().type('Extra Grande')
    cy.contains('Save changes').click()
    cy.contains('Product option has been successfully updated.').should('be.visible')
    cy.contains('GG').should('be.visible')
  })

  it('CT007 - Editar valor de opção', () => {
    cy.get('input[type="search"]').type('TAMANHO')
    cy.contains('Search').click()
    cy.get('tbody tr').first().contains('Edit').click()
    cy.get('input[placeholder="Value"]').first().clear().type('Pequeno Modificado')
    cy.contains('Save changes').click()
    cy.contains('Product option has been successfully updated.').should('be.visible')
  })

  it('CT008 - Remover valor de opção', () => {
    cy.get('input[type="search"]').type('COR')
    cy.contains('Search').click()
    cy.get('tbody tr').first().contains('Edit').click()
    cy.get('button[title="Delete"]').first().click()
    cy.contains('Save changes').click()
    cy.contains('Product option has been successfully updated.').should('be.visible')
  })

  it('CT009 - Filtrar opções por nome/código', () => {
    cy.get('input[type="search"]').type('TAMANHO')
    cy.contains('Search').click()
    cy.get('tbody tr').should('have.length.at.least', 1)
    cy.contains('TAMANHO').should('be.visible')
    cy.contains('Reset').click()
    cy.get('tbody tr').should('have.length.at.least', 1)
  })

  it('CT010 - Excluir opção de produto', () => {
    cy.contains('Create').click()
    cy.get('#sylius_product_option_code').type('DELETAR')
    cy.get('#sylius_product_option_translations_en_US_name').type('Opção para Deletar')
    cy.contains('Add value').click()
    cy.get('input[placeholder="Code"]').last().type('TESTE')
    cy.get('input[placeholder="Value"]').last().type('Valor Teste')
    cy.contains('button', 'Create').click()
    cy.contains('Product option has been successfully created.').should('be.visible')
    cy.visit('/admin/product-options/')
    cy.get('input[type="search"]').type('DELETAR')
    cy.contains('Search').click()
    cy.get('tbody tr').first().contains('Delete').click()
    cy.contains('Yes, delete').click()
    cy.contains('Product option has been successfully deleted.').should('be.visible')
    cy.contains('DELETAR').should('not.exist')
  })
})
const { Builder, By, Key, until } = require('selenium-webdriver');
const { expect } = require('chai');

describe('Seção de Opções de Produto - Admin Sylius (Selenium)', function() {
  this.timeout(30000);
  let driver;

  beforeEach(async () => {
    driver = await new Builder().forBrowser('chrome').build();
    await driver.get('http://localhost:8080/admin/login');
    await driver.findElement(By.name('_username')).sendKeys('admin@example.com');
    await driver.findElement(By.name('_password')).sendKeys('sylius');
    await driver.findElement(By.css('button[type="submit"]')).click();
    await driver.wait(until.urlContains('/admin/dashboard'), 10000);
    await driver.get('http://localhost:8080/admin/product-options/');
    await driver.wait(until.elementLocated(By.css('body')), 10000);
  });

  afterEach(async () => {
    await driver.quit();
  });

  it('CT001 - Criar opção de produto simples com sucesso', async () => {
    await driver.findElement(By.linkText('Create')).click();
    await driver.findElement(By.id('sylius_product_option_code')).sendKeys('TAMANHO_SEL');
    await driver.findElement(By.id('sylius_product_option_translations_en_US_name')).sendKeys('Tamanho Selenium');
    await driver.findElement(By.xpath("//button[contains(text(), 'Add value')]")).click();
    
    const codeInputs = await driver.findElements(By.css('input[placeholder="Code"]'));
    const valueInputs = await driver.findElements(By.css('input[placeholder="Value"]'));
    await codeInputs[codeInputs.length - 1].sendKeys('P');
    await valueInputs[valueInputs.length - 1].sendKeys('Pequeno');
    
    await driver.findElement(By.css('button[type="submit"]')).click();
    await driver.wait(until.elementLocated(By.css('.positive.message')), 5000);
    const message = await driver.findElement(By.css('.positive.message')).getText();
    expect(message).to.include('Product option has been successfully created');
  });

  it('CT002 - Criar opção com múltiplos valores', async () => {
    await driver.findElement(By.linkText('Create')).click();
    await driver.findElement(By.id('sylius_product_option_code')).sendKeys('COR_SEL');
    await driver.findElement(By.id('sylius_product_option_translations_en_US_name')).sendKeys('Cor Selenium');
    
    await driver.findElement(By.xpath("//button[contains(text(), 'Add value')]")).click();
    let codeInputs = await driver.findElements(By.css('input[placeholder="Code"]'));
    let valueInputs = await driver.findElements(By.css('input[placeholder="Value"]'));
    await codeInputs[0].sendKeys('VERMELHO');
    await valueInputs[0].sendKeys('Vermelho');
    
    await driver.findElement(By.xpath("//button[contains(text(), 'Add value')]")).click();
    codeInputs = await driver.findElements(By.css('input[placeholder="Code"]'));
    valueInputs = await driver.findElements(By.css('input[placeholder="Value"]'));
    await codeInputs[1].sendKeys('AZUL');
    await valueInputs[1].sendKeys('Azul');
    
    await driver.findElement(By.xpath("//button[contains(text(), 'Add value')]")).click();
    codeInputs = await driver.findElements(By.css('input[placeholder="Code"]'));
    valueInputs = await driver.findElements(By.css('input[placeholder="Value"]'));
    await codeInputs[2].sendKeys('VERDE');
    await valueInputs[2].sendKeys('Verde');
    
    await driver.findElement(By.css('button[type="submit"]')).click();
    await driver.wait(until.elementLocated(By.css('.positive.message')), 5000);
    const message = await driver.findElement(By.css('.positive.message')).getText();
    expect(message).to.include('successfully created');
  });

  it('CT003 - Validar campos obrigatórios ao criar opção', async () => {
    await driver.findElement(By.linkText('Create')).click();
    await driver.findElement(By.css('button[type="submit"]')).click();
    await driver.wait(until.elementLocated(By.css('.negative.message')), 5000);
    const errorMessages = await driver.findElements(By.css('.negative.message li'));
    expect(errorMessages.length).to.be.at.least(2);
    const codeField = await driver.findElement(By.id('sylius_product_option_code'));
    const codeClass = await codeField.getAttribute('class');
    expect(codeClass).to.include('error');
  });

  it('CT004 - Validar código duplicado', async () => {
    await driver.findElement(By.linkText('Create')).click();
    await driver.findElement(By.id('sylius_product_option_code')).sendKeys('TAMANHO_SEL');
    await driver.findElement(By.id('sylius_product_option_translations_en_US_name')).sendKeys('Tamanho Duplicado');
    await driver.findElement(By.xpath("//button[contains(text(), 'Add value')]")).click();
    
    const codeInputs = await driver.findElements(By.css('input[placeholder="Code"]'));
    const valueInputs = await driver.findElements(By.css('input[placeholder="Value"]'));
    await codeInputs[codeInputs.length - 1].sendKeys('G');
    await valueInputs[valueInputs.length - 1].sendKeys('Grande');
    
    await driver.findElement(By.css('button[type="submit"]')).click();
    await driver.wait(until.elementLocated(By.css('.negative.message')), 5000);
    const message = await driver.findElement(By.css('.negative.message')).getText();
    expect(message).to.include('already exists');
  });

  it('CT005 - Editar opção existente', async () => {
    const searchInput = await driver.findElement(By.css('input[type="search"]'));
    await searchInput.sendKeys('TAMANHO_SEL', Key.RETURN);
    await driver.sleep(2000);
    
    const editButtons = await driver.findElements(By.xpath("//a[contains(@href, 'edit')]"));
    await editButtons[0].click();
    
    const nameField = await driver.findElement(By.id('sylius_product_option_translations_en_US_name'));
    await nameField.clear();
    await nameField.sendKeys('Tamanho Selenium Editado');
    
    await driver.findElement(By.xpath("//button[contains(text(), 'Save changes')]")).click();
    await driver.wait(until.elementLocated(By.css('.positive.message')), 5000);
    const message = await driver.findElement(By.css('.positive.message')).getText();
    expect(message).to.include('successfully updated');
  });

  it('CT006 - Adicionar novo valor a opção existente', async () => {
    const searchInput = await driver.findElement(By.css('input[type="search"]'));
    await searchInput.sendKeys('TAMANHO_SEL', Key.RETURN);
    await driver.sleep(2000);
    
    const editButtons = await driver.findElements(By.xpath("//a[contains(@href, 'edit')]"));
    await editButtons[0].click();
    
    await driver.findElement(By.xpath("//button[contains(text(), 'Add value')]")).click();
    
    const codeInputs = await driver.findElements(By.css('input[placeholder="Code"]'));
    const valueInputs = await driver.findElements(By.css('input[placeholder="Value"]'));
    await codeInputs[codeInputs.length - 1].sendKeys('GG');
    await valueInputs[valueInputs.length - 1].sendKeys('Extra Grande');
    
    await driver.findElement(By.xpath("//button[contains(text(), 'Save changes')]")).click();
    await driver.wait(until.elementLocated(By.css('.positive.message')), 5000);
    const message = await driver.findElement(By.css('.positive.message')).getText();
    expect(message).to.include('successfully updated');
  });

  it('CT007 - Editar valor de opção', async () => {
    const searchInput = await driver.findElement(By.css('input[type="search"]'));
    await searchInput.sendKeys('TAMANHO_SEL', Key.RETURN);
    await driver.sleep(2000);
    
    const editButtons = await driver.findElements(By.xpath("//a[contains(@href, 'edit')]"));
    await editButtons[0].click();
    
    const valueInputs = await driver.findElements(By.css('input[placeholder="Value"]'));
    await valueInputs[0].clear();
    await valueInputs[0].sendKeys('Pequeno Modificado');
    
    await driver.findElement(By.xpath("//button[contains(text(), 'Save changes')]")).click();
    await driver.wait(until.elementLocated(By.css('.positive.message')), 5000);
    const message = await driver.findElement(By.css('.positive.message')).getText();
    expect(message).to.include('successfully updated');
  });

  it('CT008 - Remover valor de opção', async () => {
    const searchInput = await driver.findElement(By.css('input[type="search"]'));
    await searchInput.sendKeys('COR_SEL', Key.RETURN);
    await driver.sleep(2000);
    
    const editButtons = await driver.findElements(By.xpath("//a[contains(@href, 'edit')]"));
    await editButtons[0].click();
    
    const deleteButtons = await driver.findElements(By.css('button[title="Delete"]'));
    await deleteButtons[0].click();
    
    await driver.findElement(By.xpath("//button[contains(text(), 'Save changes')]")).click();
    await driver.wait(until.elementLocated(By.css('.positive.message')), 5000);
    const message = await driver.findElement(By.css('.positive.message')).getText();
    expect(message).to.include('successfully updated');
  });

  it('CT009 - Filtrar opções por nome/código', async () => {
    const searchInput = await driver.findElement(By.css('input[type="search"]'));
    await searchInput.sendKeys('TAMANHO_SEL', Key.RETURN);
    await driver.sleep(2000);
    
    const rows = await driver.findElements(By.css('tbody tr'));
    expect(rows.length).to.be.at.least(1);
    
    await driver.findElement(By.linkText('Reset')).click();
    await driver.sleep(2000);
    
    const allRows = await driver.findElements(By.css('tbody tr'));
    expect(allRows.length).to.be.at.least(1);
  });

  it('CT010 - Excluir opção de produto', async () => {
    await driver.findElement(By.linkText('Create')).click();
    await driver.findElement(By.id('sylius_product_option_code')).sendKeys('DELETAR_SEL');
    await driver.findElement(By.id('sylius_product_option_translations_en_US_name')).sendKeys('Opção para Deletar Selenium');
    await driver.findElement(By.xpath("//button[contains(text(), 'Add value')]")).click();
    
    const codeInputs = await driver.findElements(By.css('input[placeholder="Code"]'));
    const valueInputs = await driver.findElements(By.css('input[placeholder="Value"]'));
    await codeInputs[codeInputs.length - 1].sendKeys('TESTE');
    await valueInputs[valueInputs.length - 1].sendKeys('Valor Teste');
    
    await driver.findElement(By.css('button[type="submit"]')).click();
    await driver.wait(until.elementLocated(By.css('.positive.message')), 5000);
    
    await driver.get('http://localhost/admin/product-options/');
    
    const searchInput = await driver.findElement(By.css('input[type="search"]'));
    await searchInput.sendKeys('DELETAR_SEL', Key.RETURN);
    await driver.sleep(2000);
    
    const deleteButton = await driver.findElement(By.xpath("//button[contains(text(), 'Delete')]"));
    await deleteButton.click();
    await driver.wait(until.elementLocated(By.css('.actions .positive')), 3000);
    await driver.findElement(By.xpath("//button[contains(text(), 'Yes, delete')]")).click();
    
    await driver.wait(until.elementLocated(By.css('.positive.message')), 5000);
    const message = await driver.findElement(By.css('.positive.message')).getText();
    expect(message).to.include('successfully deleted');
    
    await driver.sleep(2000);
    const bodyText = await driver.findElement(By.css('body')).getText();
    expect(bodyText).to.not.include('DELETAR_SEL');
  });
});
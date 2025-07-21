// Pode colocar em uma pasta como com.simulador.criaturas.system.config
package com.simulador.criaturas.system.config;

public class TestConfig {

  // --- Configuração do Timeout ---
  private static final String PROP_TIMEOUT = "timeout.seconds";
  private static final long DEFAULT_TIMEOUT = 100;

  // --- Configuração da URL Base ---
  private static final String PROP_BASE_URL = "base.url";
  private static final String DEFAULT_BASE_URL = "http://localhost:3000";

  /**
   * Retorna o timeout para esperas explícitas.
   * Tenta ler de "-Dtimeout.seconds=30". Se não encontrar, usa o padrão.
   */
  public static long getTimeoutSeconds() {
    String timeoutFromProperty = System.getProperty(PROP_TIMEOUT);
    if (timeoutFromProperty != null) {
      try {
        return Long.parseLong(timeoutFromProperty);
      } catch (NumberFormatException e) {
        // Loga o erro e continua com o padrão
        System.err.println("Valor inválido para a propriedade " + PROP_TIMEOUT + ". Usando o padrão.");
      }
    }
    return DEFAULT_TIMEOUT;
  }

  /**
   * Retorna a URL base da aplicação.
   * Tenta ler de "-Dbase.url=http://meusite.com". Se não encontrar, usa o padrão.
   */
  public static String getBaseUrl() {
    return System.getProperty(PROP_BASE_URL, DEFAULT_BASE_URL);
  }
}
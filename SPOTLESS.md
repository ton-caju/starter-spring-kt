# Spotless - Code Formatting

Este projeto utiliza [Spotless](https://github.com/diffplug/spotless) 8.1.0 com [ktfmt](https://github.com/facebook/ktfmt) (kotlinlang style) para Kotlin e [Google Java Format](https://github.com/google/google-java-format) para Java, mantendo a formatação consistente do código.

## Comandos Disponíveis

### Verificar formatação
```bash
./gradlew spotlessCheck
```
Verifica se todos os arquivos seguem as regras de formatação sem modificá-los.

### Aplicar formatação
```bash
./gradlew spotlessApply
```
Aplica automaticamente as correções de formatação em todos os arquivos.

### Verificar módulo específico
```bash
./gradlew :domain:spotlessCheck
./gradlew :driver:rest-server:spotlessCheck
```

### Aplicar formatação em módulo específico
```bash
./gradlew :domain:spotlessApply
./gradlew :driver:rest-server:spotlessApply
```

## Configuração

O Spotless está configurado no arquivo `build.gradle.kts` principal com as seguintes características:

### Kotlin
- **Formatador**: ktfmt (kotlinlang style)
- **Estilo**: Segue o estilo oficial do Kotlin (kotlinlang)
- **Indentação**: 4 espaços (padrão kotlinlang)
- **Características**:
  - Formatação automática de imports
  - Quebras de linha consistentes
  - Alinhamento de parâmetros

### Kotlin Gradle (*.gradle.kts)
- **Formatador**: ktfmt (kotlinlang style)
- Mesma configuração dos arquivos Kotlin

### Java
- **Formatador**: Google Java Format
- Segue o guia de estilo do Google para Java

### Arquivos alvo
- Kotlin: Todos os arquivos `*.kt` em `src/`
- Kotlin Gradle: Arquivos `*.gradle.kts`
- Java: Todos os arquivos `*.java` em `src/`
- Exclui arquivos em `build/`

## Integração com CI/CD

Recomenda-se adicionar `./gradlew spotlessCheck` no pipeline de CI para garantir que o código está formatado corretamente antes do merge.

## IDE Integration

### IntelliJ IDEA
1. Instale o plugin "ktfmt" (opcional)
2. Configure: Settings → Editor → Code Style → Kotlin
3. Ative "Set from... → Predefined Style → Kotlin style guide"
4. Para formatar automaticamente ao salvar:
   - Settings → Tools → Actions on Save
   - Marque "Reformat code"

**Alternativa**: Execute `./gradlew spotlessApply` antes de commitar

### VSCode
1. Instale a extensão "Kotlin Language"
2. Configure formatação automática no `settings.json`:
```json
{
  "editor.formatOnSave": true,
  "[kotlin]": {
    "editor.defaultFormatter": "fwcd.kotlin"
  }
}
```

**Alternativa**: Execute `./gradlew spotlessApply` antes de commitar

## Troubleshooting

### Conflitos com o IDE
Se o IDE auto-formatou diferente do Spotless, execute `./gradlew spotlessApply` para corrigir.

### Build falhando no CI
Certifique-se de executar `./gradlew spotlessApply` antes de fazer push do código.

### Diferenças de formatação após pull
Execute `./gradlew spotlessApply` após fazer pull para aplicar a formatação mais recente.

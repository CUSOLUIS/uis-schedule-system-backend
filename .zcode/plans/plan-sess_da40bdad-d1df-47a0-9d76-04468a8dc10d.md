## Desinstalar Visual Studio Build Tools (C++) para liberar espacio en disco C

### Pasos:

1. **Ejecutar el instalador de Visual Studio**:
   - Abrir `"C:\Program Files (x86)\Microsoft Visual Studio\Installer\vs_installer.exe"` o buscar "Instalador de Visual Studio" en el menú Inicio.

2. **Desinstalar Build Tools**:
   - En el instalador, ubicar la entrada "Visual Studio Build Tools" (2019/2022).
   - Hacer clic en **"Desinstalar"**.

3. **Alternativa por línea de comandos** (si el GUI falla):
   - Ejecutar:
     ```
     "C:\Program Files (x86)\Microsoft Visual Studio\Installer\vs_installer.exe" modify --installPath "C:\Program Files (x86)\Microsoft Visual Studio\2022\BuildTools" --uninstall --passive
     ```
   - Si no existe en 2022, intentar con `2019\BuildTools`.

4. **Verificar espacio liberado**:
   - Comprobar el espacio disponible en disco C tras la desinstalación.

**Requiere permisos de administrador. Puede solicitar reinicio del equipo.**
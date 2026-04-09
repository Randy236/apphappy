# Checklist de Despliegue y Pruebas

Guia corta para presentar avances del curso de despliegue de software y pruebas unitarias.

## 1) Flujo Git recomendado

- Trabajar nuevas tareas en rama `develop`.
- Subir avances frecuentes con commits pequenos.
- Pasar cambios estables a `main`.

Comandos base:

```bash
git checkout develop
git add .
git commit -m "feat: modulo o mejora"
git push -u origin develop
```

Cuando algo ya este listo para entrega:

```bash
git checkout main
git merge develop
git push
```

## 2) Checklist de despliegue local

- [ ] MySQL levantado.
- [ ] Script `server/schema.sql` ejecutado.
- [ ] Migracion `server/migrations/001_usuarios_active_token.sql` aplicada (si corresponde).
- [ ] API corriendo con `npm start` en `server/`.
- [ ] `happyJump.api.baseUrl` configurado para emulador o celular fisico.
- [ ] App instalada y conectando a backend sin errores.

## 3) Checklist minimo de pruebas

- [ ] Login correcto con usuario valido.
- [ ] Bloqueo de sesion duplicada: segundo dispositivo recibe mensaje esperado.
- [ ] Cierre de sesion libera el acceso en otro dispositivo.
- [ ] Registro de reserva de cancha correcto.
- [ ] Registro de reserva de salon correcto (solo trabajador).
- [ ] Perfil permite cambiar PIN.
- [ ] Reportes cargan por filtro Hoy/Semana/Mes.

## 4) Evidencia sugerida para el profesor

- Captura de commits en GitHub por cada avance.
- Captura de pruebas unitarias ejecutadas.
- Captura de app en 2 dispositivos validando sesion unica.
- Breve reporte de incidencias encontradas y como se resolvieron.


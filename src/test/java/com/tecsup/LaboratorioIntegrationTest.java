package com.tecsup;
import com.tecsup.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:tests;DB_CLOSE_DELAY=-1","spring.jpa.hibernate.ddl-auto=create-drop"}) @AutoConfigureMockMvc
class LaboratorioIntegrationTest {
 @Autowired MockMvc mvc;@Autowired ProductoRepository productos;@Autowired AuditoriaRepository auditoria;
 private final String body="{\"nombre\":\"Disco duro\",\"precio\":2500,\"stock\":10}";
 @BeforeEach void limpiar(){auditoria.deleteAll();productos.deleteAll();}
 @Test void crudYAuditoria() throws Exception {
  String response=mvc.perform(post("/api/productos").header("Usuario","Ana").header("Rol","USER").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
  Long id=new com.fasterxml.jackson.databind.ObjectMapper().readTree(response).get("id").asLong();
  mvc.perform(get("/api/productos").header("Usuario","Luis").header("Rol","USER")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
  mvc.perform(put("/api/productos/"+id).header("Usuario","Ricardo").header("Rol","ADMIN").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
  mvc.perform(delete("/api/productos/"+id).header("Usuario","Ricardo").header("Rol","ADMIN")).andExpect(status().isNoContent());
  var logs=auditoria.findAll();assertThat(logs).extracting("accion").containsExactly("CREAR","LISTAR","ACTUALIZAR","ELIMINAR");
  assertThat(logs.get(0).getUsuario()).isEqualTo("Ana (USER)");assertThat(logs.get(1).getDetalle()).isEqualTo("Cantidad de productos: 1");assertThat(logs.get(2).getDetalle()).endsWith(id.toString());assertThat(logs.get(3).getDetalle()).isEqualTo("Se eliminó producto ID: "+id);
 }
 @Test void permisosYErroresPersistentes() throws Exception {
  mvc.perform(get("/api/productos")).andExpect(status().isUnauthorized());
  mvc.perform(get("/api/productos").header("Usuario","Ana").header("Rol","ADMIN")).andExpect(status().isForbidden());
  mvc.perform(delete("/api/productos/999").header("Usuario","Ana").header("Rol","USER")).andExpect(status().isForbidden());
  mvc.perform(delete("/api/productos/999").header("Usuario","Ricardo").header("Rol","ADMIN")).andExpect(status().isNotFound());
  assertThat(auditoria.findAll()).hasSize(4).extracting("accion").containsOnly("ERROR");assertThat(productos.count()).isZero();
 }
 @Test void validacionYConsultaAuditoria() throws Exception {
  mvc.perform(post("/api/productos").header("Usuario","Ana").header("Rol","USER").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"\",\"precio\":-1,\"stock\":-1}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.nombre").exists());
  assertThat(productos.count()).isZero();
  mvc.perform(get("/api/auditoria").header("Usuario","Ana").header("Rol","USER")).andExpect(status().isForbidden());
  mvc.perform(get("/api/auditoria").header("Usuario","Ricardo").header("Rol","ADMIN")).andExpect(status().isOk());
 }
}

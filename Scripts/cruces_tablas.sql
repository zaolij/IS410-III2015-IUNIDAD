SELECT 	A.CODIGO_EVENTO AS CODIGO,
		A.NOMBRE_EVENTO,
        A.DIRECCION,
        A.FECHA,
        A.CANTIDAD_INVITADOS,
        B.NOMBRE_TIPO_EVENTO,
        C.NOMBRE_ESTADO,
        D.NOMBRE_MUNICIPIO,
        E.NOMBRE_DEPARTAMENTO,
        F.NOMBRE_PAIS
FROM TBL_EVENTOS A
INNER JOIN TBL_TIPOS_EVENTOS B
ON (A.CODIGO_TIPO_EVENTO = B.CODIGO_TIPO_EVENTO)
INNER JOIN TBL_ESTADOS C
ON (A.CODIGO_ESTADO = C.CODIGO_ESTADO)
INNER JOIN TBL_MUNICIPIOS D 
ON (A.CODIGO_MUNICIPIO = D.CODIGO_MUNICIPIO)
INNER JOIN TBL_DEPARTAMENTOS E
ON (D.CODIGO_DEPARTAMENTO = E.CODIGO_DEPARTAMENTO)
INNER JOIN TBL_PAISES F
ON (E.CODIGO_PAIS = F.CODIGO_PAIS)
WHERE CODIGO_EVENTO = 1;


/*MOSTRAR LOS INVITADOS Y LOS EVENTOS*/

SELECT B.NOMBRE_EVENTO,
		C.NOMBRE_INVITADO
FROM TBL_INVITADOS_X_EVENTO A
INNER JOIN TBL_EVENTOS B
ON (A.CODIGO_EVENTO = B.CODIGO_EVENTO)
INNER JOIN TBL_INVITADOS C
ON (A.CODIGO_INVITADO = C.CODIGO_INVITADO)
ORDER BY A.CODIGO_EVENTO; /*ASC: ASCENDENTE, DESC: DESCENDENTE*/
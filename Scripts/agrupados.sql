Agrupados

SELECT CAMPOS,FUNCIONES_DE_AGREGACION()
FROM TABLA
GROUP BY CAMPOS;

--Cantidad de eventos por fecha
SELECT FECHA, COUNT(*) AS CANTIDAD_EVENTOS 
FROM TBL_EVENTOS
GROUP BY FECHA
ORDER BY FECHA;

--Cantidad de eventos por tipo
SELECT B.nombre_tipo_evento, count(*) as cantidad_eventos
FROM tbl_eventos A
INNER JOIN tbl_tipos_eventos B
ON (A.codigo_tipo_evento = B.codigo_tipo_evento)
GROUP BY B.nombre_tipo_evento;


SELECT DATE_FORMAT(FECHA,'%Y%m'), COUNT(*)
FROM TBL_EVENTOS
GROUP BY DATE_FORMAT(FECHA,'%Y%m');

SELECT SUM(CODIGO_EVENTO)
FROM TBL_EVENTOS;

SELECT 	MAX(CODIGO_EVENTO), 
		MIN(CODIGO_EVENTO)
FROM TBL_EVENTOS;


INDX_DWH_TBL_PASAJEROS_FECHA
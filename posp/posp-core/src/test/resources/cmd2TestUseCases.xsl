<xsl:stylesheet version="2.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="UTF-8" indent="yes" />
	<xsl:param name="_demoSocketServerHandler_encoding"/>
	<xsl:param name="_demoSocketServerHandler_encoding"/>
<xsl:template match="/">
{
	
	"name":"<xsl:value-of select="$_demoSocketServerHandler_encoding"/>",
	"requestJsonString":"<xsl:value-of select="$_demoSocketServerHandler_encoding"/>",
	"test":"<xsl:value-of select="original"/>"
}
	</xsl:template>
</xsl:stylesheet>
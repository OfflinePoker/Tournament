<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:hatoka="xalan://de.hatoka.common.capi.app.xslt.Lib"
  exclude-result-prefixes="hatoka"
>
  <xsl:import href="de/hatoka/common/capi/app/xslt/ui.xsl" />
  <xsl:param name="localizer" />
  <xsl:param name="uriInfo" />
  <!-- BEGIN OUTPUT -->
  <xsl:output method="html" encoding="UTF-8" indent="yes" />
  <xsl:template match="/" xmlns="http://www.w3.org/1999/xhtml">
    <form method="POST" action="createPlayer">
      <xsl:call-template name="input">
        <xsl:with-param name="name">name</xsl:with-param>
        <xsl:with-param name="placeholderKey">placeholder.name.player</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="button">
        <xsl:with-param name="name">create</xsl:with-param>
      </xsl:call-template>
    </form>
    <xsl:if test="tournamentPlayerListModel/unassignedPlayers" >
    <h2>
      <xsl:value-of select="hatoka:getText($localizer, 'info.unassigned.player', 'Assign xyz')" />
    </h2>
    <form method="POST" action="assignPlayer">
      <div class="input">
        <select name="playerID">
          <xsl:for-each select="tournamentPlayerListModel/unassignedPlayers" xmlns="http://www.w3.org/1999/xhtml">
            <option>
              <xsl:attribute name="value"><xsl:value-of select="id" /></xsl:attribute>
              <xsl:value-of select="name" />
            </option>
          </xsl:for-each>
        </select>
      </div>
      <xsl:call-template name="button">
        <xsl:with-param name="name">buyin</xsl:with-param>
      </xsl:call-template>
    </form>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
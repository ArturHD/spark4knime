/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   16.12.2011 (hofer): created
 */
package de.pavloff.spark4knime.jsnippet.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.pavloff.spark4knime.PairRddCell;
import de.pavloff.spark4knime.PairRddValue;
import de.pavloff.spark4knime.RddCell;
import de.pavloff.spark4knime.RddValue;
import de.pavloff.spark4knime.jsnippet.expression.TypeException;
import de.pavloff.spark4knime.jsnippet.type.data.BooleanValueToJava;
import de.pavloff.spark4knime.jsnippet.type.data.DataValueToJava;
import de.pavloff.spark4knime.jsnippet.type.data.DateAndTimeValueToJava;
import de.pavloff.spark4knime.jsnippet.type.data.DoubleValueToJava;
import de.pavloff.spark4knime.jsnippet.type.data.IntValueToJava;
import de.pavloff.spark4knime.jsnippet.type.data.JavaToBooleanCell;
import de.pavloff.spark4knime.jsnippet.type.data.JavaToDataCell;
import de.pavloff.spark4knime.jsnippet.type.data.JavaToDateAndTimeCell;
import de.pavloff.spark4knime.jsnippet.type.data.JavaToDoubleCell;
import de.pavloff.spark4knime.jsnippet.type.data.JavaToIntCell;
import de.pavloff.spark4knime.jsnippet.type.data.JavaToLongCell;
import de.pavloff.spark4knime.jsnippet.type.data.JavaToPairRddCell;
import de.pavloff.spark4knime.jsnippet.type.data.JavaToRddCell;
import de.pavloff.spark4knime.jsnippet.type.data.JavaToStringCell;
import de.pavloff.spark4knime.jsnippet.type.data.JavaToXMLCell;
import de.pavloff.spark4knime.jsnippet.type.data.LongValueToJava;
import de.pavloff.spark4knime.jsnippet.type.data.PairRDDValueToJava;
import de.pavloff.spark4knime.jsnippet.type.data.RDDValueToJava;
import de.pavloff.spark4knime.jsnippet.type.data.StringValueToJava;
import de.pavloff.spark4knime.jsnippet.type.data.ToStringToJava;
import de.pavloff.spark4knime.jsnippet.type.data.XMLValueToJava;
import de.pavloff.spark4knime.jsnippet.type.flowvar.DoubleFlowVarToJava;
import de.pavloff.spark4knime.jsnippet.type.flowvar.IntFlowVarToJava;
import de.pavloff.spark4knime.jsnippet.type.flowvar.StringFlowVarToJava;
import de.pavloff.spark4knime.jsnippet.type.flowvar.TypeConverter;
import org.knime.core.data.BooleanValue;
import org.knime.core.data.DataType;
import org.knime.core.data.DataValue;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.date.DateAndTimeValue;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.xml.XMLCell;
import org.knime.core.data.xml.XMLValue;
import org.knime.core.node.workflow.FlowVariable.Type;

/**
 * A central place for type converters for data cells and flow variables.
 * 
 * @author Heiko Hofer
 * @author Oleg Pavlov, University of Heidelberg
 */
public final class TypeProvider {
	private static TypeProvider provider;
	private Map<Class<? extends DataValue>, DataValueToJava> m_dataValueConverter;

	private Map<DataType, DataValueToJava> m_dataValueToJava;
	private Map<DataType, DataValueToJava> m_collDataValueToJava;
	private Map<DataType, JavaToDataCell> m_javaToCell;
	private Map<DataType, JavaToDataCell> m_javaToListCell;

	private Map<Type, TypeConverter> m_flowVarConverter;
	private ToStringToJava m_toStringToJava;

	/** Prevent creation of class instances. */
	private TypeProvider() {
		m_dataValueConverter = new LinkedHashMap<Class<? extends DataValue>, DataValueToJava>();
		m_dataValueConverter.put(BooleanValue.class, new BooleanValueToJava());
		m_dataValueConverter.put(IntValue.class, new IntValueToJava());
		m_dataValueConverter.put(DoubleValue.class, new DoubleValueToJava());
		m_dataValueConverter.put(LongValue.class, new LongValueToJava());
		m_dataValueConverter.put(StringValue.class, new StringValueToJava());
		m_dataValueConverter.put(XMLValue.class, new XMLValueToJava());
		m_dataValueConverter.put(DateAndTimeValue.class,
				new DateAndTimeValueToJava());
		// RDD part
		m_dataValueConverter.put(RddValue.class, new RDDValueToJava());
		m_dataValueConverter.put(PairRddValue.class, new PairRDDValueToJava());

		m_dataValueToJava = new LinkedHashMap<DataType, DataValueToJava>();
		m_dataValueToJava.put(StringCell.TYPE,
				m_dataValueConverter.get(StringValue.class));
		m_dataValueToJava.put(IntCell.TYPE,
				new MultiValueToJava(m_dataValueConverter.get(IntValue.class),
						m_dataValueConverter.get(DoubleValue.class)));
		m_dataValueToJava.put(LongCell.TYPE,
				m_dataValueConverter.get(LongValue.class));
		m_dataValueToJava.put(DoubleCell.TYPE,
				m_dataValueConverter.get(DoubleValue.class));
		m_dataValueToJava.put(BooleanCell.TYPE,
				m_dataValueConverter.get(BooleanValue.class));
		m_dataValueToJava.put(XMLCell.TYPE,
				new MultiValueToJava(m_dataValueConverter.get(XMLValue.class),
						m_dataValueConverter.get(StringValue.class)));
		m_dataValueToJava.put(DateAndTimeCell.TYPE,
				m_dataValueConverter.get(DateAndTimeValue.class));
		// RDD part
		m_dataValueToJava.put(RddCell.TYPE,
				m_dataValueConverter.get(RddValue.class));
		m_dataValueToJava.put(PairRddCell.TYPE,
				m_dataValueConverter.get(PairRddValue.class));

		// add list cell converters
		m_collDataValueToJava = new LinkedHashMap<DataType, DataValueToJava>();
		for (DataType type : m_dataValueToJava.keySet()) {
			DataValueToJava dvtj = m_dataValueToJava.get(type);
			m_collDataValueToJava.put(type, new ListCellToJava(dvtj));
		}

		// the converters from java to a data cell
		m_javaToCell = new LinkedHashMap<DataType, JavaToDataCell>();
		m_javaToCell.put(BooleanCell.TYPE, new JavaToBooleanCell());
		m_javaToCell.put(IntCell.TYPE, new JavaToIntCell());
		m_javaToCell.put(DoubleCell.TYPE, new JavaToDoubleCell());
		m_javaToCell.put(LongCell.TYPE, new JavaToLongCell());
		m_javaToCell.put(StringCell.TYPE, new JavaToStringCell());
		m_javaToCell.put(XMLCell.TYPE, new JavaToXMLCell());
		m_javaToCell.put(DateAndTimeCell.TYPE, new JavaToDateAndTimeCell());
		// RDD part
		m_javaToCell.put(RddCell.TYPE, new JavaToRddCell());
		m_javaToCell.put(PairRddCell.TYPE, new JavaToPairRddCell());

		// add list cell converter
		m_javaToListCell = new LinkedHashMap<DataType, JavaToDataCell>();
		for (DataType type : m_javaToCell.keySet()) {
			JavaToDataCell dvtj = m_javaToCell.get(type);
			m_javaToListCell.put(type, new JavaToListCell(dvtj));
		}

		// Converters for flow variables
		m_flowVarConverter = new LinkedHashMap<Type, TypeConverter>();
		m_flowVarConverter.put(Type.DOUBLE, new DoubleFlowVarToJava());
		m_flowVarConverter.put(Type.INTEGER, new IntFlowVarToJava());
		m_flowVarConverter.put(Type.STRING, new StringFlowVarToJava());

		// default converter using toString() when no special converter for
		// a data cell can be found
		m_toStringToJava = new ToStringToJava();

	}

	/**
	 * Get default type provider.
	 * 
	 * @return the default instance
	 */
	public static TypeProvider getDefault() {
		if (null == provider) {
			provider = new TypeProvider();
		}
		return provider;
	}

	/**
	 * Get list of possible data types for input columns.
	 * 
	 * @return the list of data types
	 */
	public Collection<DataType> getInputDataTypes() {
		return m_dataValueToJava.keySet();
	}

	/**
	 * Get list of possible data types for output columns.
	 * 
	 * @return the list of data types
	 */
	public Collection<DataType> getOutputDataTypes() {
		return m_javaToCell.keySet();
	}

	/**
	 * Get list of possible flow variable types.
	 * 
	 * @return the list of flow variables types
	 */
	public Collection<Type> getTypes() {
		return m_flowVarConverter.keySet();
	}

	/**
	 * Get the type converter for the give flow variable type.
	 * 
	 * @param type
	 *            the flow variable type
	 * @return the type converter for the given flow variable type
	 */
	public TypeConverter getTypeConverter(final Type type) {
		return m_flowVarConverter.get(type);
	}

	/**
	 * Get the type converter to convert a data value of the given type to a
	 * java object.
	 * 
	 * @param type
	 *            the type to be converted
	 * @param isCollectionType
	 *            if collection of the given type should be converted
	 * @return the type converter
	 */
	public DataValueToJava getDataValueToJava(final DataType type,
			final boolean isCollectionType) {
		// if this is a known data type
		if (isCollectionType) {
			if (m_collDataValueToJava.containsKey(type)) {
				return m_collDataValueToJava.get(type);
			}
		} else {
			if (m_dataValueToJava.containsKey(type)) {
				return m_dataValueToJava.get(type);
			}
		}

		// check data value converters this type is compatible with
		List<DataValueToJava> compatibleList = new ArrayList<DataValueToJava>();
		for (Class<? extends DataValue> dv : m_dataValueConverter.keySet()) {
			if (type.isCompatible(dv)) {
				compatibleList.add(m_dataValueConverter.get(dv));
			}
		}
		DataValueToJava[] compatible = compatibleList
				.toArray(new DataValueToJava[compatibleList.size()]);
		if (compatible.length > 0) {
			// remember compatible types
			DataValueToJava cellToJava = new MultiValueToJava(compatible);
			m_dataValueToJava.put(type, cellToJava);
			DataValueToJava listCellToJava = new ListCellToJava(cellToJava);
			m_collDataValueToJava.put(type, listCellToJava);
			return type.isCollectionType() ? listCellToJava : cellToJava;
		} else {
			return m_toStringToJava;
		}
	}

	/**
	 * Get an type converter that can be utilized to create data cells of the
	 * given type or collection cells with elements of the given type.
	 * 
	 * @param type
	 *            the type
	 * @param isCollectionType
	 *            if collection cells should be created
	 * @return the converter from java class to a data cell.
	 */
	public JavaToDataCell getJavaToDataCell(final DataType type,
			final boolean isCollectionType) {
		if (isCollectionType) {
			if (m_javaToListCell.containsKey(type)) {
				return m_javaToListCell.get(type);
			}
		} else {
			if (m_javaToCell.containsKey(type)) {
				return m_javaToCell.get(type);
			}
		}
		// data type is not known
		throw new TypeException("The data type " + type + " is not supported.");
	}
}

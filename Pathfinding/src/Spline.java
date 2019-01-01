
import java.util.Arrays;

public class Spline {
	public static void main(String[] args) {
		double[][] xy = { { 1.7, -1.92 }, { 8.64, -2.98 }, { 12.32, 1.78 }, { 8.76, 0.66 }, { 6.56, 4.8 },
				{ 3.64, 3.36 }, { 3.96, 1.28 } };
		double[] distance = new double[xy.length - 1];
		double total = 0;
		for (int i = 0; i < distance.length; i++) {
			distance[i] = Math.sqrt(Math.pow((xy[i + 1][0] - xy[i][0]), 2) + Math.pow((xy[i + 1][1] - xy[i][1]), 2));
			total += distance[i];
		}
		System.out.println(Arrays.toString(distance));
		double[][] tx = new double[xy.length][2];
		double[][] ty = new double[xy.length][2];
		ty[0][0] = tx[0][0] = 0;
		ty[xy.length - 1][0] = tx[xy.length - 1][0] = 1;
		for (int i = 1; i < distance.length; i++) {
			double actualDis = 0;
			for (int i1 = 0; i1 < i; i1++) {
				actualDis += distance[i1];
			}
			ty[i][0] = tx[i][0] = actualDis / total;
		}
		for (int i = 0; i < xy.length; i++) {
			tx[i][1] = xy[i][0];
			ty[i][1] = xy[i][1];
		}
		System.out.println(Arrays.deepToString(tx));
		System.out.println(Arrays.deepToString(ty));

		calculateSpline(tx, ty);
	}

	public static void calculateSpline(double[][] tx, double[][] ty) {
		int k = ty.length - 1;
		double[] dty, cty, bty, aty, hty, zty, firstdty;
		firstdty = new double[k + 1];
		dty = new double[k + 1];
		bty = new double[k + 1];
		cty = new double[k + 1];
		aty = new double[k + 1];
		hty = new double[k + 1];
		zty = new double[k + 1];
		double[] dtx, ctx, btx, atx, htx, ztx;
		dtx = new double[k + 1];
		btx = new double[k + 1];
		ctx = new double[k + 1];
		atx = new double[k + 1];
		htx = new double[k + 1];
		ztx = new double[k + 1];
		ztx = splineClamped(tx, -10, -10);
		zty = splineClamped(ty, 7.3, -11.8);
		for (int i = 0; i < k + 1; i++) {
			dtx[i] = tx[i][1];
			dty[i] = ty[i][1];
		}
		for (int i = 0; i < k; i++) {
			htx[i] = tx[i + 1][0] - tx[i][0];
			hty[i] = ty[i + 1][0] - ty[i][0];
		}
		for (int i = 0; i < k; i++) {
			atx[i] = (ztx[i + 1] - ztx[i]) / (6 * htx[i]);
			btx[i] = ztx[i] / 2;
			ctx[i] = -(htx[i] * ztx[i + 1]) / 6 - (htx[i] * ztx[i]) / 3 + (dtx[i + 1] - dtx[i]) / htx[i];
			aty[i] = (zty[i + 1] - zty[i]) / (6 * hty[i]);
			bty[i] = zty[i] / 2;
			cty[i] = -(hty[i] * zty[i + 1]) / 6 - (hty[i] * zty[i]) / 3 + (dty[i + 1] - dty[i]) / hty[i];
		}
		for (int i = 0; i < k; i++) {
			String res = "curve(";
			res += atx[i] + "(t - " + tx[i][0] + ")^3 + " + btx[i] + "(t - " + tx[i][0] + ")^2 + " + ctx[i] + "(t - "
					+ tx[i][0] + ") + " + dtx[i];
			res += ", ";
			res += aty[i] + "(t - " + ty[i][0] + ")^3 + " + bty[i] + "(t - " + ty[i][0] + ")^2 + " + cty[i] + "(t - "
					+ ty[i][0] + ") + " + dty[i];
			res += ", t, " + tx[i][0] + ", " + tx[i + 1][0] + " )";
			System.out.println(res);
		}
		System.out.println(Arrays.toString(htx));
		System.out.println(Arrays.toString(hty));
	}

	public static double[] splineClamped(double[][] xy, double x, double y) {
		int k = xy.length - 1;
		double[] h = new double[k + 1];
		double[] u = new double[k + 1];
		double[] l = new double[k + 1];
		double[] d = new double[k + 1];
		double[] z = new double[k + 1];

		for (int i = 1; i < k + 1; i++) {
			h[i] = xy[i][0] - xy[i - 1][0];
		}
		for (int i = 1; i < k; i++) {
			u[i] = h[i] / (h[i] + h[i + 1]);
			l[i] = h[i + 1] / (h[i] + h[i + 1]);
			d[i] = 6 / (h[i] + h[i + 1]) * ((xy[i + 1][1] - xy[i][1]) / h[i + 1] - (xy[i][1] - xy[i - 1][1]) / h[i]);
		}
		System.out.println(Arrays.toString(d));

		l[0] = 1;
		u[k] = 1;
//		d[0] = 6 / (h[1] + h[2]) * ((xy[1][1] - xy[0][1]) / h[1]);
//		d[k] = 6 / (h[k - 1] + h[k]) * ((xy[k][1] - xy[k - 1][1]) / h[k - 1]);
		d[0] = 6 / h[1] * ((xy[1][1] - xy[0][1]) / h[1] + x);
		d[k] = 6 / h[k] * (y - (xy[k][1] - xy[k - 1][1]) / h[k]);
		double[] b = new double[k + 1];
		b[0] = 2;
		for (int i = 1; i < k + 1; i++) {
			double wx = u[i] / b[i - 1];
			b[i] = 2 - wx * l[i - 1];
			d[i] = d[i] - wx * d[i - 1];
		}
		z[k] = d[k] / b[k];
		for (int i = k - 1; i > -1; i--) {
			z[i] = (d[i] - l[i] * z[i + 1]) / b[i];
		}

		return z;
	}
}
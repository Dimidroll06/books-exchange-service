import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";

export default function Footer() {
  return (
    <Box
      sx={{ mt: 4, py: 2, bgcolor: "background.paper", textAlign: "center" }}
    >
      <Typography variant="body2" color="text.secondary">
        © {new Date().getFullYear()} Книжный обмен
      </Typography>
    </Box>
  );
}

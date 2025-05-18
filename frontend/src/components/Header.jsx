import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import AccountCircle from "@mui/icons-material/AccountCircle";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Button from "@mui/material/Button";
import InputBase from "@mui/material/InputBase";
import SearchIcon from "@mui/icons-material/Search";
import Box from "@mui/material/Box";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { getUser } from "../services/authService";

export default function Header() {
  const user = getUser();
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState(null);
  const [search, setSearch] = useState("");

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogin = () => {
    navigate("/login");
  };

  const handleProfile = () => {
    navigate("/profile/"+user.id);
    handleClose();
  };

  const handleMyExchanges = () => {
    navigate("/my-exchanges");
    handleClose();
  };

  const handleMyBooks = () => {
    navigate("/profile#books");
    handleClose();
  };

  const handleAdminPanel = () => {
    navigate("/admin/panel");
    handleClose();
  };

  const handleSearch = (e) => {
    e.preventDefault();
    navigate(`/?q=${encodeURIComponent(search)}`);
  };

  return (
    <AppBar position="fixed" color="primary">
      <Toolbar>
        <Typography
          variant="h6"
          component="div"
          sx={{ flexGrow: 1, cursor: "pointer" }}
          onClick={() => navigate("/")}
        >
          Книги
        </Typography>
        <Box
          component="form"
          onSubmit={handleSearch}
          sx={{
            mr: 2,
            display: "flex",
            alignItems: "center",
            bgcolor: "background.paper",
            borderRadius: 1,
            px: 1,
          }}
        >
          <SearchIcon />
          <InputBase
            placeholder="Поиск…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            sx={{ ml: 1, flex: 1 }}
            inputProps={{ "aria-label": "search" }}
          />
        </Box>
        {!user ? (
          <Button color="inherit" onClick={handleLogin}>
            Войти
          </Button>
        ) : (
          <div>
            <IconButton
              size="large"
              edge="end"
              color="inherit"
              onClick={handleMenu}
            >
              <AccountCircle />
            </IconButton>
            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleClose}
            >
              <MenuItem onClick={handleProfile}>Профиль</MenuItem>
              <MenuItem onClick={handleMyExchanges}>Мои сделки</MenuItem>
              <MenuItem onClick={handleMyBooks}>Мои книги</MenuItem>
              {user.is_admin && (
                <MenuItem onClick={handleAdminPanel}>
                  Панель администратора
                </MenuItem>
              )}
            </Menu>
          </div>
        )}
      </Toolbar>
    </AppBar>
  );
}
